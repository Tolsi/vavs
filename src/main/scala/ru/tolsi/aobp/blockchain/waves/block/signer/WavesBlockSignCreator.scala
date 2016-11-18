package ru.tolsi.aobp.blockchain.waves.block.signer

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.base.SignCreator
import ru.tolsi.aobp.blockchain.waves.{Sign, SignCreator, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.transaction.SignedTransaction

private[signer] class WavesBlockSignCreator(implicit signedTransactionsSerializer: BytesSerializer[Seq[SignedTransaction[WavesBlockChain#T]]]) extends SignCreator[WavesBlockChain#B] {
  override def createSign(block: WavesBlockChain#B): Sign[WavesBlockChain#B] = {
    val txBytes: Array[Byte] = signedTransactionsSerializer.serialize(block.transactions)
    val txBytesWithSize = Bytes.ensureCapacity(Ints.toByteArray(txBytes.length), 4, 0) ++ txBytes

    val consensusBytes = Bytes.ensureCapacity(Longs.toByteArray(block.baseTarget), 8, 0) ++
      block.generationSignature.value
    val consensusBytesWithSize = Bytes.ensureCapacity(Ints.toByteArray(consensusBytes.length), 4, 0) ++ consensusBytes

    Sign(Array(block.version) ++
      Bytes.ensureCapacity(Longs.toByteArray(block.timestamp), 8, 0) ++
      block.reference.value ++
      consensusBytesWithSize ++
      txBytesWithSize ++
      block.generator.publicKey)
  }
}
