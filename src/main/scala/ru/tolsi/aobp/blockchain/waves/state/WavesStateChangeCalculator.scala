package ru.tolsi.aobp.blockchain.waves.state

import ru.tolsi.aobp.blockchain.base.{BlockChain, StateChange}
import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves.{Waves, WavesBlockChain}

abstract class StateChangeCalculator[BC <: BlockChain] {
  def calculateStateChanges(b: BC#B): Seq[StateChange[BC]]
}

class WavesStateChangeCalculator extends StateChangeCalculator[WavesBlockChain] {
  def calculateStateChanges(b: WavesBlockChain#B): Seq[StateChange[WavesBlockChain]] = {
    val blockFee = b.transactions.map(t => (t.signed.feeCurrency, t.signed.fee))
    b.transactions.map(_.signed).flatMap {
      case t: GenesisTransaction => Seq(
        StateChange[WavesBlockChain]((t.recipient, Waves), t.quantity),
        StateChange[WavesBlockChain]((b.generator.address, t.feeCurrency), t.fee)
      )
      case t: PaymentTransaction => Seq(
        StateChange[WavesBlockChain]((t.sender.address, t.feeCurrency), -t.fee),
        StateChange[WavesBlockChain]((t.sender.address, Waves), -t.quantity),
        StateChange[WavesBlockChain]((t.recipient, Waves), t.quantity),
        StateChange[WavesBlockChain]((b.generator.address, t.feeCurrency), t.fee)
      )
      case t: IssueTransaction => Seq(
        StateChange[WavesBlockChain]((t.sender.address, t.feeCurrency), -t.fee),
        StateChange[WavesBlockChain]((t.sender.address, t.issue.currency.b), t.quantity),
        StateChange[WavesBlockChain]((b.generator.address, t.feeCurrency), t.fee)
      )
      case t: ReissueTransaction => Seq(
        StateChange[WavesBlockChain]((t.sender.address, t.feeCurrency), -t.fee),
        StateChange[WavesBlockChain]((t.sender.address, t.issue.currency.b), t.quantity),
        StateChange[WavesBlockChain]((b.generator.address, t.feeCurrency), t.fee)
      )
      case t: TransferTransaction => Seq(
        StateChange[WavesBlockChain]((t.sender.address, t.feeCurrency), -t.fee),
        StateChange[WavesBlockChain]((t.sender.address, t.transfer.currency.fold(identity, identity)), -t.quantity),
        StateChange[WavesBlockChain]((t.recipient, t.transfer.currency.fold(identity, identity)), t.quantity),
        StateChange[WavesBlockChain]((b.generator.address, t.feeCurrency), t.fee)
      )
    }
  }
}
