package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves.transaction.signer.wavesTransactionSigner

package object validator {
  private[validator] implicit val genesisTransactionValidator = new GenesisTransactionValidator
  private[validator] implicit val issueTransactionValidator = new IssueTransactionValidator
  private[validator] implicit val reissueTransactionValidator = new ReissueTransactionValidator
  private[validator] implicit val paymentTransactionValidator = new PaymentTransactionValidator
  private[validator] implicit val transferTransactionValidator = new TransferTransactionValidator
  private[validator] implicit val unsignedTransactionValidator = new UnsignedTransactionValidator
  private[validator] implicit val signedTransactionValidator = new SignedTransactionValidator(wavesTransactionSigner, unsignedTransactionValidator)
  def signedTransactionWithTimeValidator(timestamp: Long) = new SignedTransactionWithTimeValidators(timestamp, wavesTransactionSigner)
}
