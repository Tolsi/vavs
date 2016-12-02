package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.SignedBlock
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Block

class BlockMessageSerializer(signedBlockSerializer: BytesSerializer[SignedBlock[WavesBlock]]) extends NetworkMessageBytesSerializer[Block] {
  override def serialize(blockMessage: Block): Array[Byte] = {
    val magicBytes = NetworkMessage.MagicBytes
    val contentId = blockMessage.contentId
    val blockBytes = signedBlockSerializer.serialize(blockMessage.block)
    val payloadChecksum = calculateDataChecksum(blockBytes)
    val packetLength = 17 + blockBytes.length
    Bytes.concat(intBytesEnsureCapacity(packetLength), magicBytes, Array(contentId), intBytesEnsureCapacity(blockBytes.length), payloadChecksum, blockBytes)
  }
}
