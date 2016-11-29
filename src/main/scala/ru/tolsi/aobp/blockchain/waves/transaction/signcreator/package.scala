package ru.tolsi.aobp.blockchain.waves.transaction

package object signcreator {
  private[transaction] implicit val genesisTransactionSignCreator = new GenesisTransactionDataForSignCreator
  private[transaction] implicit val paymentTransactionSignCreator = new PaymentTransactionDataForSignCreator
  private[transaction] implicit val issueTransactionSignCreator = new IssueTransactionDataForSignCreator
  private[transaction] implicit val reissueTransactionSignCreator = new ReissueTransactionDataForSignCreator
  private[transaction] implicit val transferTransactionSignCreator = new TransferTransactionDataForSignCreator
  implicit val wavesTransactionSignCreator = new WavesTransactionDataForSignCreator
}
