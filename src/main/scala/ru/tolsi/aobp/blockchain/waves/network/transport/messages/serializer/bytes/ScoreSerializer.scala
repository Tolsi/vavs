package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer._
import ru.tolsi.aobp.blockchain.waves.SignedBlock
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Score

class ScoreSerializer(signedBlockSerializer: BytesSerializer[SignedBlock[WavesBlock]])
  extends NetworkMessageSerializer[Score] {
  override def serialize(score: Score): Array[Byte] = {
    val magicBytes = NetworkMessage.MagicBytes
    val contentId = score.contentId
    val scoreBytes = score.score.toByteArray
    val payloadChecksum = calculateDataChecksum(scoreBytes)
    val packetLength = 17 + scoreBytes.length
    Bytes.concat(
      intBytesEnsureCapacity(packetLength),
      magicBytes,
      Array(contentId),
      payloadChecksum,
      scoreBytes
    )
  }
}
