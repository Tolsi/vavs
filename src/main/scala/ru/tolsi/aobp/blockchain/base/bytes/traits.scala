package ru.tolsi.aobp.blockchain.base.bytes

trait BytesSerializable

trait BytesSerializer[BS <: BytesSerializable] {
  def serialize(obj: BS): Array[Byte]
}

trait BytesDeserializer[BS <: BytesSerializable] {
  def deserialize(array: Array[Byte]): BS
}
