package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves.transaction.signcreator._

package object signer {
  private[signer] implicit val genesisTransactionSigner = new GenesisTransactionSigner
  private[signer] implicit val paymentTransactionSigner = new PaymentTransactionSigner
  private[signer] implicit val issueTransactionSigner = new IssueTransactionSigner
  private[signer] implicit val reissueTransactionSigner = new ReissueTransactionSigner
  private[signer] implicit val transferTransactionSigner = new TransferTransactionSigner
  val wavesTransactionSigner = new WavesTransactionSigner
}
