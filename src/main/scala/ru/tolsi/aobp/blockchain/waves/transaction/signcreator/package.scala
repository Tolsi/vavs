package ru.tolsi.aobp.blockchain.waves.transaction

package object signcreator {
  private[transaction] implicit val genesisTransactionSignCreator = new GenesisTransactionSignCreator
  private[transaction] implicit val paymentTransactionSignCreator = new PaymentTransactionSignCreator
  private[transaction] implicit val issueTransactionSignCreator = new IssueTransactionSignCreator
  private[transaction] implicit val reissueTransactionSignCreator = new ReissueTransactionSignCreator
  private[transaction] implicit val transferTransactionSignCreator = new TransferTransactionSignCreator
  implicit val wavesTransactionSignCreator = new WavesTransactionSignCreator
}
