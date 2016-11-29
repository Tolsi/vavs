package ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes

import ru.tolsi.aobp.blockchain.base.bytes.{BytesSerializer, SeqBytesSerializer}
import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, WavesTransaction}

class SignedTransactionsSeqBytesSerializer(tbs: BytesSerializer[WavesSignedTransaction[WavesTransaction]],
                                           sbs: SeqBytesSerializer[WavesSignedTransaction[WavesTransaction]]) extends BytesSerializer[Seq[WavesSignedTransaction[WavesTransaction]]] {
  override def serialize(txs: Seq[WavesSignedTransaction[WavesTransaction]]): Array[Byte] = {
    sbs.serialize(txs)
  }
}
