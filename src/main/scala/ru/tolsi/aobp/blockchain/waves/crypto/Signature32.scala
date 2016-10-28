package ru.tolsi.aobp.blockchain.waves.crypto

import scorex.crypto.encode.Base58

class Signature32(val bytes: Array[Byte]) {

  assert(bytes.length == Signature32.Length,
    s"Incorrect signature length, length of given array of bytes is ${bytes.length}, expected length is ${Signature64.Length}")

  override def hashCode(): Int = {
    LyHash.compute(bytes)
  }

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: Signature32 => bytes.sameElements(other.bytes)
    case _ => false
  }

  override def toString: String = Base58.encode(bytes)

}

object Signature32 {

  val Length = 32

  def apply(bytes: Array[Byte]): Signature32 = new Signature32(bytes)

}
