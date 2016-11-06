package ru.tolsi.aobp.blockchain.waves.transaction.signer

import com.google.common.primitives.Shorts
import ru.tolsi.aobp.blockchain.base.{Signable, Signature, Signer, WithByteArraySing}
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

abstract class WavesSigner[S <: Signable with WithByteArraySing, SI <: Signature[Array[Byte]]] extends Signer[WavesBlockChain, S, SI] {
  // todo move to bytes serializer
  protected def arrayWithSize(b: Array[Byte]): Array[Byte] = Shorts.toByteArray(b.length.toShort) ++ b

  protected def booleanWithByte(b: Boolean): Byte = (if (b) 1 else 0).toByte

  protected def writeArrayOption(a: Option[Array[Byte]]): Array[Byte] = a.map(a => (1: Byte) +: a).getOrElse(Array(0: Byte))
}
