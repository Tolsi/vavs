package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.primitives.{Bytes, Ints}
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer._
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Checkpoint

class CheckpointSerializer extends NetworkMessageSerializer[Checkpoint] {
  override def serialize(checkpoint: Checkpoint): Array[Byte] = {
    val magicBytes = NetworkMessage.MagicBytes
    val contentId = checkpoint.contentId
    val payload = Bytes.concat(Ints.toByteArray(checkpoint.checkpoints.size), checkpoint.checkpoints
      .foldLeft(Array.empty[Byte]) { case (bs, (height, blockSignature)) => Bytes.concat(bs, longBytesEnsureCapacity(height), blockSignature.value) })
    val packageLength = 17
    val packageChecksum = calculateDataChecksum(payload)
    Bytes.concat(
      intBytesEnsureCapacity(packageLength),
      magicBytes,
      Array(contentId),
      intBytesEnsureCapacity(payload.length),
      packageChecksum,
      payload
    )
  }
}
