package ru.tolsi.aobp.blockchain.base.bytes

import com.google.common.primitives.{Bytes, Ints}

trait BytesSerializable

trait BytesSerializer[BS] {
  def serialize(obj: BS): Array[Byte]
}

class SeqBytesSerializer[BS <: BytesSerializable](implicit bs: BytesSerializer[BS]) extends BytesSerializer[Seq[BS]] {
  override def serialize(seq: Seq[BS]): Array[Byte] = {
    val txsBytes = seq.map(bs.serialize).foldLeft(Array.empty[Byte]) {
      case (obj, result) => Bytes.concat(result, obj)
    }
    Bytes.concat(Ints.toByteArray(seq.size), txsBytes)
  }
}

trait BytesDeserializer[BS] {
  def deserialize(array: Array[Byte]): BS
}

class SeqBytesDeserializer[BS <: BytesSerializable] extends BytesDeserializer[Seq[BS]] {
  override def deserialize(obj: Array[Byte]): Seq[BS] = {
    // todo read size and then objects
    ???
  }
}
