package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.base.bytes.BytesDeserializer
import ru.tolsi.aobp.blockchain.base.bytes.BytesDeserializer._
import ru.tolsi.aobp.blockchain.waves.SignedBlock
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Block
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes.NetworkMessageBytesDeserializer._

import scala.util.{Failure, Success, Try}


case class InvalidBlockLength(message: String) extends RuntimeException(message)

object BlockMessageDeserializer {
  def validateBlockLength(bytes: Array[Byte]): Try[Array[Byte]] = {
    val length = bytes.length
    for {
      (lengthFromPacket, bytesAfterBlockLength) <- longBytes(bytes)
      res <- if (lengthFromPacket == length) {
        Success(bytesAfterBlockLength)
      } else {
        Failure(InvalidPayloadLength(s"Invalid block length: $lengthFromPacket != $length"))
      }
    } yield res
  }
}

class BlockMessageDeserializer(signedBlockDeserializer: BytesDeserializer[SignedBlock[WavesBlock]]) extends NetworkMessageBytesDeserializer[Block] {

  import BlockMessageDeserializer._

  override def deserialize(bytes: Array[Byte]): Try[Block] = {
    for {
      bytesAfterPacketLength <- validatePackageSize(bytes)
      bytesAfterMagicBytes <- validateMagicBytes(bytesAfterPacketLength)
      bytesAfterContentId <- validateContentId(bytesAfterMagicBytes, Block.ContentId)
      bytesAfterPayloadLength <- validatePayloadLength(bytesAfterContentId)
      bytesAfterPayloadChecksum <- {
        for {
          (payloadChecksum, bytesAfterPayloadChecksum) <- arrayWithSize(bytesAfterPayloadLength, 4)
          res <- validateDataChecksum(bytesAfterPayloadChecksum, payloadChecksum)
        } yield res
      }
      bytesAfterBlockBytesLength <- validateBlockLength(bytesAfterPayloadChecksum)
      signedBlock <- signedBlockDeserializer.deserialize(bytesAfterBlockBytesLength)
    } yield Block(signedBlock)
  }
}
