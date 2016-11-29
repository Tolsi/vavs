package ru.tolsi.aobp.blockchain.waves.transaction.serializer

import ru.tolsi.aobp.blockchain.waves.transaction.signcreator.wavesTransactionSignCreator
import ru.tolsi.aobp.blockchain.base.bytes.seqBytesSerializer
import ru.tolsi.aobp.blockchain.waves.SignedTransaction
import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, WavesTransaction}

package object bytes {
  private[bytes] implicit val signedTransactionBytesSerializer = new SignedTransactionBytesSerializer(wavesTransactionSignCreator)
  val signedTransactionBytesSeqSerializer = new SignedTransactionsSeqBytesSerializer(signedTransactionBytesSerializer, seqBytesSerializer[WavesSignedTransaction[WavesTransaction]])
}
