package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer.intBytesEnsureCapacity
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.GetSignatures

class GetBlocksSignaturesSerializer extends NetworkMessageSerializer[GetSignatures] {
  override def serialize(getSignatures: GetSignatures): Array[Byte] = {
    val magicBytes = NetworkMessage.MagicBytes

    val contentId = getSignatures.contentId

    val payload = Bytes.concat(intBytesEnsureCapacity(getSignatures.signatures.size), getSignatures.signatures.foldLeft(Array.empty[Byte]) { case (bytes, signature) =>
      Bytes.concat(bytes, signature.value)
    })

    val payloadLength = intBytesEnsureCapacity(payload.length)

    val payloadChecksum = calculateDataChecksum(payload)

    val packageLength = 17 + getSignatures.signatures.size * 64

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
