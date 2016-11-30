package ru.tolsi.aobp.blockchain.waves.block.signer

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.DataForSignCreator
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, WavesTransaction}

private[block] class WavesBlockDataForSignCreator(signedTransactionsSerializer: BytesSerializer[Seq[WavesSignedTransaction[WavesTransaction]]]) extends DataForSignCreator[WavesBlock] {
  override def serialize(block: WavesBlock): Array[Byte] = {
    val txBytes = signedTransactionsSerializer.serialize(block.transactions)
    val txBytesWithSize = intBytesEnsureCapacity(txBytes.length) ++ txBytes

    val consensusBytes = longBytesEnsureCapacity(block.baseTarget) ++
      block.generationSignature.value
    val consensusBytesWithSize = intBytesEnsureCapacity(consensusBytes.length) ++ consensusBytes

    Array(block.version) ++
      longBytesEnsureCapacity(block.timestamp) ++
      block.reference.value ++
      consensusBytesWithSize ++
      txBytesWithSize ++
      block.generator.publicKey
  }
}
