package ru.tolsi.aobp.blockchain.waves.state

import ru.tolsi.aobp.blockchain.base.{BlockChain, StateChange}
import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves.{Address, Waves, WavesBlockChain}

abstract class StateChangeCalculator[BC <: BlockChain] {
  def calculateStateChanges(t: BC#T): Seq[StateChange[BC]]
  def calculateStateChanges(txs: Seq[BC#T]): Seq[StateChange[BC]]
  def calculateStateChanges(b: BC#B): Seq[StateChange[BC]]
}

class WavesStateChangeCalculator extends StateChangeCalculator[WavesBlockChain] {
  def calculateStateChanges(b: WavesBlockChain#B): Seq[StateChange[WavesBlockChain]] = {
    val blockFee = b.transactions.map(t => (t.signed.feeCurrency, t.signed.fee))
    val txs = b.transactions.map(_.signed)
    calculateStateChanges(txs) ++ calculateGeneratorFee(b.generator.address, txs)
  }

  private[state] def calculateGeneratorFee(generatorAddress: Address, txs: Seq[WavesTransaction]): Seq[StateChange[WavesBlockChain]] = {
    txs.map(t => StateChange[WavesBlockChain]((generatorAddress, t.feeCurrency), t.fee))
  }

  override def calculateStateChanges(t: WavesTransaction): Seq[StateChange[WavesBlockChain]] = t match {
    case t: GenesisTransaction => Seq(
      StateChange[WavesBlockChain]((t.recipient, Waves), t.quantity)
    )
    case t: PaymentTransaction => Seq(
      StateChange[WavesBlockChain]((t.sender.address, t.feeCurrency), -t.fee),
      StateChange[WavesBlockChain]((t.sender.address, Waves), -t.quantity),
      StateChange[WavesBlockChain]((t.recipient, Waves), t.quantity)
    )
    case t: IssueTransaction => Seq(
      StateChange[WavesBlockChain]((t.sender.address, t.feeCurrency), -t.fee),
      StateChange[WavesBlockChain]((t.sender.address, t.issue.currency.b), t.quantity)
    )
    case t: ReissueTransaction => Seq(
      StateChange[WavesBlockChain]((t.sender.address, t.feeCurrency), -t.fee),
      StateChange[WavesBlockChain]((t.sender.address, t.issue.currency.b), t.quantity)
    )
    case t: TransferTransaction => Seq(
      StateChange[WavesBlockChain]((t.sender.address, t.feeCurrency), -t.fee),
      StateChange[WavesBlockChain]((t.sender.address, t.transfer.currency.fold(identity, identity)), -t.quantity),
      StateChange[WavesBlockChain]((t.recipient, t.transfer.currency.fold(identity, identity)), t.quantity)
    )
  }

  override def calculateStateChanges(txs: Seq[WavesTransaction]): Seq[StateChange[WavesBlockChain]] = {
    txs.flatMap(calculateStateChanges)
  }
}
