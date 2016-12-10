package ru.tolsi.aobp.blockchain.base.bytes

import com.google.common.primitives.{Bytes, Ints, Longs, Shorts}

import scala.util.Try

trait BytesSerializable

object BytesSerializer {
  def intBytesEnsureCapacity(i: Int): Array[Byte] = Bytes.ensureCapacity(Ints.toByteArray(i), 4, 0)
  def longBytesEnsureCapacity(l: Long): Array[Byte] = Bytes.ensureCapacity(Longs.toByteArray(l), 8, 0)
}

trait BytesSerializer[BS] {
  def serialize(obj: BS): Array[Byte]
}

case class SerializationError(cause: Throwable) extends RuntimeException(cause)
class SeqBytesSerializer[BS <: BytesSerializable](implicit bs: BytesSerializer[BS]) extends BytesSerializer[Seq[BS]] {
  override def serialize(seq: Seq[BS]): Array[Byte] = {
    val txsBytes = seq.map(bs.serialize).foldLeft(Array.empty[Byte]) {
      case (obj, result) => Bytes.concat(result, obj)
    }
    Bytes.concat(Ints.toByteArray(seq.size), txsBytes)
  }
}

object BytesDeserializer {
  type BytesArrayRead[N] = Try[(N, Array[Byte])]
  def intBytes(bytes: Array[Byte]): BytesArrayRead[Int] = Try(Ints.fromByteArray(bytes.take(4)), bytes.drop(4))
  def longBytes(bytes: Array[Byte]): BytesArrayRead[Long] = Try(Longs.fromByteArray(bytes.take(8)), bytes.drop(8))
  def arrayWithSize(bytes: Array[Byte], size: Int): BytesArrayRead[Array[Byte]] = Try(bytes, bytes.take(size))
  def byte(bytes: Array[Byte]): BytesArrayRead[Byte] = Try(bytes.head, bytes.drop(1))
  def skip(bytes: Array[Byte], skip: Int): BytesArrayRead[Unit] = Try((), bytes.drop(skip))
}

case class DeserializationError(cause: Throwable) extends RuntimeException(cause)

trait BytesDeserializer[BS] {
  def deserialize(array: Array[Byte]): Try[BS]
}

class SeqBytesDeserializer[BS <: BytesSerializable] extends BytesDeserializer[Seq[BS]] {
  override def deserialize(obj: Array[Byte]): Try[Seq[BS]] = {
    // todo read size and then objects
    ???
  }
}
