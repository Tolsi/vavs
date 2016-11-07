package ru.tolsi.aobp.blockchain.base.bytes

trait BytesSerializable

trait BytesSerializer[BS] {
  def serialize(obj: BS): Array[Byte]
}

abstract class SeqBytesSerializer[BS <: BytesSerializable] extends BytesSerializer[Seq[BS]] {
  override def serialize(obj: Seq[BS]): Array[Byte] = {
    // todo write size and then sized size objects
    ???
  }
}

trait BytesDeserializer[BS] {
  def deserialize(array: Array[Byte]): BS
}

abstract class SeqBytesDeserializer[BS <: BytesSerializable] extends BytesDeserializer[Seq[BS]] {
  override def deserialize(obj: Array[Byte]): Seq[BS] = {
    // todo write size and then sized size objects
    ???
  }
}
