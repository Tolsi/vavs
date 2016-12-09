package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer.intBytesEnsureCapacity
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.GetBlock

class GetBlockSerializer extends NetworkMessageSerializer[GetBlock] {
  override def serialize(getBlock: GetBlock): Array[Byte] = {
    val magicBytes = NetworkMessage.MagicBytes
    val contentId = getBlock.contentId
    val payload = getBlock.blockSignature.value
    val payloadLength = intBytesEnsureCapacity(payload.length)
    val payloadChecksum = calculateDataChecksum(payload)
    val packageLength = 81
    Bytes.concat(
      intBytesEnsureCapacity(packageLength),
      magicBytes,
      Array(contentId),
      payloadLength,
      payloadChecksum,
      payload
    )
  }
}
