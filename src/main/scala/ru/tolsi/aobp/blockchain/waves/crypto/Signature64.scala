package ru.tolsi.aobp.blockchain.waves.crypto

import scorex.crypto.encode.Base58
import scorex.crypto.signatures.Curve25519
import ru.tolsi.aobp.blockchain.base.{Signature64 => BaseSignature64}

class Signature64(val bytes: Array[Byte]) extends BaseSignature64(bytes) {
  assert(bytes.length == Signature64.Length,
    s"Incorrect signature length, length of given array of bytes is ${bytes.length}, expected length is ${Curve25519.SignatureLength}")

  override def hashCode(): Int = {
    LyHash.compute(bytes)
  }

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: Signature64 => bytes.sameElements(other.bytes)
    case _ => false
  }

  override def toString: String = Base58.encode(bytes)
}

object Signature64 {

  val Length = Curve25519.SignatureLength

  def apply(bytes: Array[Byte]): Signature64 = new Signature64(bytes)

}
