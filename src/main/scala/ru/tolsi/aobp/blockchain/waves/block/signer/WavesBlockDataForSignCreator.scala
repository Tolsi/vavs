package ru.tolsi.aobp.blockchain.waves.block.signer

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.DataForSignCreator
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer._

private[block] class WavesBlockDataForSignCreator(signedTransactionsSerializer: BytesSerializer[Seq[WavesSignedTransaction[WavesTransaction]]]) extends DataForSignCreator[WavesBlock] {
  override def serialize(block: WavesBlock): Array[Byte] = {
    val txBytes = signedTransactionsSerializer.serialize(block.transactions)
    val txBytesWithSize = Bytes.concat(intBytesEnsureCapacity(txBytes.length), txBytes)

    val consensusBytes = Bytes.concat(longBytesEnsureCapacity(block.baseTarget), block.generationSignature.value)
    val consensusBytesWithSize = Bytes.concat(intBytesEnsureCapacity(consensusBytes.length), consensusBytes)

    Bytes.concat(Array(block.version),
      longBytesEnsureCapacity(block.timestamp),
      block.reference.value,
      consensusBytesWithSize,
      txBytesWithSize,
      block.generator.publicKey)
  }
}
