package ru.tolsi.aobp.blockchain.waves.storage.state.calculation

import ru.tolsi.aobp.blockchain.waves.{Waves, WavesBlockChain}

trait WavesStateChangeCalculator {
  self: WavesBlockChain =>

  class WavesStateChangeCalculator  {
    def calculateStateChanges(b: B): Seq[StateChange] = {
      val blockFee = b.transactions.map(t => (t.signed.feeCurrency, t.signed.fee))
      val feeBalanceChanges = blockFee.groupBy(_._1).mapValues(_.map(_._2).sum).map {
        case (currency, value) => StateChange((b.generator.address, currency), value)
      }
      val balanceUpdates = b.transactions.map(_.signed).flatMap {
        case t: GenesisTransaction => Seq(
          StateChange((t.recipient, Waves), t.quantity)
        )
        case t: PaymentTransaction => Seq(
          StateChange((t.sender.address, Waves), -t.quantity),
          StateChange((t.recipient, Waves), t.quantity)
        )
        case t: IssueTransaction => Seq(
          StateChange((t.sender.address, t.issue.currency.b), t.quantity)
        )
        case t: ReissueTransaction => Seq(
          StateChange((t.sender.address, t.issue.currency.b), t.quantity)
        )
        case t: TransferTransaction => Seq(
          StateChange((t.sender.address, t.transfer.currency.fold(identity, identity)), -t.quantity),
          StateChange((t.recipient, t.transfer.currency.fold(identity, identity)), t.quantity)
        )
      }
      balanceUpdates ++ feeBalanceChanges
    }
  }

}
