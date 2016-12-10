package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.SignedBlock
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Score

import scala.util.Try

class ScoreDeserializer(signedBlockSerializer: BytesSerializer[SignedBlock[WavesBlock]]) extends NetworkMessageBytesDeserializer[Score] {
  override def deserialize(array: Array[Byte]): Try[Score] = ???
}
