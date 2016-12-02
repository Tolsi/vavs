package ru.tolsi.aobp.blockchain.waves.transaction.serializer

import ru.tolsi.aobp.blockchain.waves.transaction.signcreator.{genesisTransactionSignCreator, issueTransactionSignCreator, reissueTransactionSignCreator, transferTransactionSignCreator}
import ru.tolsi.aobp.blockchain.base.bytes.seqBytesSerializer
import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, WavesTransaction}

package object bytes {
  private[bytes] implicit val signedPaymentTransactionBytesSeqSerializer = new SignedPaymentTransactionBytesSerializer()
  private[bytes] implicit val signedIssueTransactionBytesSeqSerializer = new SignedIssueTransactionBytesSerializer(issueTransactionSignCreator)
  private[bytes] implicit val signedReissueTransactionBytesSeqSerializer = new SignedReissueTransactionBytesSerializer(reissueTransactionSignCreator)
  private[bytes] implicit val signedTransferTransactionBytesSeqSerializer = new SignedTransferTransactionBytesSerializer(transferTransactionSignCreator)
  private[waves] implicit val signedTransactionBytesSerializer = new SignedTransactionBytesSerializer(genesisTransactionSignCreator)
  private[waves] val signedTransactionBytesSeqSerializer = new SignedTransactionsSeqBytesSerializer(signedTransactionBytesSerializer, seqBytesSerializer[WavesSignedTransaction[WavesTransaction]])
}
