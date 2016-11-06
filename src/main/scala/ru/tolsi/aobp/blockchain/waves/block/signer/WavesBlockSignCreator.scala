package ru.tolsi.aobp.blockchain.waves.block.signer

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.base.{ArraySign, ArraySignCreator}
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

object WavesBlockSignCreator extends ArraySignCreator[WavesBlockChain#B] {
  override def createSign(block: WavesBlockChain#B): ArraySign = {
    val txBytes: Array[Byte] = ???
    //implicitly[BytesSerializer[Seq[Signed[SignedTransaction[T], Signature64]]]].serialize(block.transactions)
    val txBytesWithSize = Bytes.ensureCapacity(Ints.toByteArray(txBytes.length), 4, 0) ++ txBytes

    val consensusBytes = Bytes.ensureCapacity(Longs.toByteArray(block.baseTarget), 8, 0) ++
      block.generationSignature.value
    val consensusBytesWithSize = Bytes.ensureCapacity(Ints.toByteArray(consensusBytes.length), 4, 0) ++ consensusBytes

    ArraySign(Array(block.version) ++
      Bytes.ensureCapacity(Longs.toByteArray(block.timestamp), 8, 0) ++
      block.reference.value ++
      consensusBytesWithSize ++
      txBytesWithSize ++
      block.generator.publicKey)
  }
}
