package ru.tolsi.aobp.blockchain.waves.crypto

import scorex.crypto.encode.Base58
import scorex.crypto.signatures.Curve25519

class Signature64(val bytes: Array[Byte]) extends KryoDataType[Signature64] {
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

  override def compare(a: scala.Any, b: scala.Any): Int = (a, b) match {
    case (first: Signature64, second: Signature64) =>
      // todo is it good?
      new String(first.bytes).compareTo(new String(second.bytes))
    case _ => throw new UnsupportedOperationException
  }
}

object Signature64 {

  val Length = Curve25519.SignatureLength

  def apply(bytes: Array[Byte]): Signature64 = new Signature64(bytes)

}
