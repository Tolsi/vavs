package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer.intBytesEnsureCapacity
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.BlocksSignatures

class BlocksSignaturesSerializer extends NetworkMessageSerializer[BlocksSignatures] {
  override def serialize(blocksSignatures: BlocksSignatures): Array[Byte] = {
    val magicBytes = NetworkMessage.MagicBytes

    val contentId = blocksSignatures.contentId

    val payload =
      Bytes.concat(intBytesEnsureCapacity(blocksSignatures.signatures.size),
        blocksSignatures.signatures.foldLeft(Array.empty[Byte]) { case (bytes, signature) =>
          Bytes.concat(bytes, signature.value)
        })

    val payloadLength = intBytesEnsureCapacity(payload.length)

    val payloadChecksum = calculateDataChecksum(payload)

    val packageLength = 17 + blocksSignatures.signatures.size * 64

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
