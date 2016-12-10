package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.base.bytes.BytesDeserializer
import ru.tolsi.aobp.blockchain.base.bytes.BytesDeserializer.{arrayWithSize, longBytes, _}
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.NetworkMessageChecksum
import scorex.crypto.encode.Base58


case class InvalidPackageLength(message: String) extends RuntimeException(message)

case class InvalidMagicBytes(message: String) extends RuntimeException(message)

case class InvalidContentId(message: String) extends RuntimeException(message)

case class InvalidPayloadLength(message: String) extends RuntimeException(message)

import scala.util.{Failure, Success, Try}

object NetworkMessageBytesDeserializer {
  def validatePackageSize(bytes: Array[Byte]): Try[Array[Byte]] = {
    val length = bytes.length
    for {
      (lengthFromPacket, bytesAfterPacketLength) <- longBytes(bytes)
      res <- if (lengthFromPacket == length) {
        Success(bytesAfterPacketLength)
      } else {
        Failure(InvalidPackageLength(s"Invalid package length: $lengthFromPacket != $length"))
      }
    } yield res
  }

  def validateMagicBytes(bytes: Array[Byte]): Try[Array[Byte]] = {
    for {(magicBytes, bytesAfterMagicBytes) <- arrayWithSize(bytes, 4)
         res <- if (magicBytes sameElements NetworkMessage.MagicBytes) {
           Success(bytesAfterMagicBytes)
         } else {
           Failure(InvalidMagicBytes(s"Invalid magic bytes: ${Base58.encode(magicBytes)} != ${Base58.encode(NetworkMessage.MagicBytes)}"))
         }} yield res
  }

  def validateContentId(bytes: Array[Byte], correctContentId: Byte): Try[Array[Byte]] = {
    for {
      (contentId, bytesAfterContentId) <- byte(bytes)
      res <- if (contentId == correctContentId) {
        Success(bytesAfterContentId)
      } else {
        Failure(InvalidContentId(s"Invalid content id: $contentId != $correctContentId"))
      }
    } yield res
  }

  def validatePayloadLength(bytes: Array[Byte]): Try[Array[Byte]] = {
    val length = bytes.length
    for {
      (lengthFromPacket, bytesAfterPayloadLength) <- longBytes(bytes)
      res <- if (lengthFromPacket == length) {
        Success(bytesAfterPayloadLength)
      } else {
        Failure(InvalidPayloadLength(s"Invalid payload length: $lengthFromPacket != $length"))
      }
    } yield res
  }
}

abstract class NetworkMessageBytesDeserializer[NM <: NetworkMessage] extends BytesDeserializer[NM] with NetworkMessageChecksum
