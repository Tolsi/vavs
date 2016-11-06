package ru.tolsi.aobp.blockchain.waves

import com.google.common.primitives.Shorts
import ru.tolsi.aobp.blockchain.base._

abstract class WavesSigner[S <: Signable with WithByteArraySing, SV <: Signed[S, SI], SI <: Signature[Array[Byte]]] extends Signer[WavesBlockChain, S, SV, SI] {
  // todo move to bytes serializer
  protected def arrayWithSize(b: Array[Byte]): Array[Byte] = Shorts.toByteArray(b.length.toShort) ++ b

  protected def booleanWithByte(b: Boolean): Byte = (if (b) 1 else 0).toByte

  protected def writeArrayOption(a: Option[Array[Byte]]): Array[Byte] = a.map(a => (1: Byte) +: a).getOrElse(Array(0: Byte))
}
