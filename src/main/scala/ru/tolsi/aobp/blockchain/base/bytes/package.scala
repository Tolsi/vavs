package ru.tolsi.aobp.blockchain.base

package object bytes {
  def seqBytesSerializer[BS <: BytesSerializable](implicit bs: BytesSerializer[BS]) = new SeqBytesSerializer[BS]
  def seqBytesDeserializer[BS <: BytesSerializable](implicit bs: BytesSerializer[BS]) = new SeqBytesDeserializer[BS]
}
