package ru.tolsi.aobp.blockchain.waves

trait WavesStateChangeCalculator {
  self: WavesBlockChain =>

  object WavesStateChangeCalculator {
    def calculateStateChanges(b: B): Seq[StateChange] = {
      val blockFee = b.transactions.map(t => (t.signed.feeCurrency, t.signed.fee))
      b.transactions.map(_.signed).flatMap {
        case t: GenesisTransaction => Seq(
          StateChange((t.recipient, Waves), t.quantity),
          StateChange((b.generator.address, t.feeCurrency), t.fee)
        )
        case t: PaymentTransaction => Seq(
          StateChange((t.sender.address, t.feeCurrency), -t.fee),
          StateChange((t.sender.address, Waves), -t.quantity),
          StateChange((t.recipient, Waves), t.quantity),
          StateChange((b.generator.address, t.feeCurrency), t.fee)
        )
        case t: IssueTransaction => Seq(
          StateChange((t.sender.address, t.feeCurrency), -t.fee),
          StateChange((t.sender.address, t.issue.currency.b), t.quantity),
          StateChange((b.generator.address, t.feeCurrency), t.fee)
        )
        case t: ReissueTransaction => Seq(
          StateChange((t.sender.address, t.feeCurrency), -t.fee),
          StateChange((t.sender.address, t.issue.currency.b), t.quantity),
          StateChange((b.generator.address, t.feeCurrency), t.fee)
        )
        case t: TransferTransaction => Seq(
          StateChange((t.sender.address, t.feeCurrency), -t.fee),
          StateChange((t.sender.address, t.transfer.currency.fold(identity, identity)), -t.quantity),
          StateChange((t.recipient, t.transfer.currency.fold(identity, identity)), t.quantity),
          StateChange((b.generator.address, t.feeCurrency), t.fee)
        )
      }
    }
  }

}
