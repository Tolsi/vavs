package ru.tolsi.aobp.blockchain.waves.state

import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves._
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock

abstract class StateChangeCalculator {
  def calculateStateChanges(t: WavesTransaction): Seq[StateChange]
  def calculateStateChanges(txs: Seq[WavesTransaction]): Seq[StateChange]
  def calculateStateChanges(b: WavesBlock): Seq[StateChange]
}

class WavesStateChangeCalculator extends StateChangeCalculator {
  def calculateStateChanges(b: WavesBlock): Seq[StateChange] = {
    val blockFee = b.transactions.map(t => (t.signed.feeCurrency, t.signed.fee))
    val txs = b.transactions.map(_.signed)
    calculateStateChanges(txs) ++ calculateGeneratorFee(b.generator.address, txs)
  }

  private[state] def calculateGeneratorFee(generatorAddress: Address, txs: Seq[WavesTransaction]): Seq[StateChange] = {
    txs.map(t => StateChange(BalanceAccount(generatorAddress, t.feeCurrency), t.fee))
  }

  override def calculateStateChanges(t: WavesTransaction): Seq[StateChange] = t match {
    case t: GenesisTransaction => Seq(
      StateChange(BalanceAccount(t.recipient, Waves), t.quantity)
    )
    case t: PaymentTransaction => Seq(
      StateChange(BalanceAccount(t.sender.address, t.feeCurrency), -t.fee),
      StateChange(BalanceAccount(t.sender.address, Waves), -t.quantity),
      StateChange(BalanceAccount(t.recipient, Waves), t.quantity)
    )
    case t: IssueTransaction => Seq(
      StateChange(BalanceAccount(t.sender.address, t.feeCurrency), -t.fee),
      StateChange(BalanceAccount(t.sender.address, t.issue.currency.value), t.quantity)
    )
    case t: ReissueTransaction => Seq(
      StateChange(BalanceAccount(t.sender.address, t.feeCurrency), -t.fee),
      StateChange(BalanceAccount(t.sender.address, t.issue.currency.value), t.quantity)
    )
    case t: TransferTransaction => Seq(
      StateChange(BalanceAccount(t.sender.address, t.feeCurrency), -t.fee),
      StateChange(BalanceAccount(t.sender.address, t.transfer.currency.fold(identity, identity)), -t.quantity),
      StateChange(BalanceAccount(t.recipient, t.transfer.currency.fold(identity, identity)), t.quantity)
    )
  }

  override def calculateStateChanges(txs: Seq[WavesTransaction]): Seq[StateChange] = {
    txs.flatMap(calculateStateChanges)
  }
}
