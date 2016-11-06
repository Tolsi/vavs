package ru.tolsi.aobp.blockchain.waves.transaction

object TransactionType extends Enumeration {
  val GenesisTransaction = Value(1)
  val PaymentTransaction = Value(2)
  val IssueTransaction = Value(3)
  val TransferTransaction = Value(4)
  val ReissueTransaction = Value(5)
}
