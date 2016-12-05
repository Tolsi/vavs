package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.GetPeers

class GetPeersSerializer(implicit wbc: WavesBlockChain) extends NetworkMessageBytesSerializer[GetPeers] {
  override def serialize(getPeers: GetPeers): Array[Byte] = {
    val magicBytes = NetworkMessage.MagicBytes
    val contentId = getPeers.contentId
    val emptyPayloadLength = intBytesEnsureCapacity(0)
    val emptyPayloadChecksum = calculateDataChecksum(Array.empty)
    val packageLength = 17
    Bytes.concat(
      intBytesEnsureCapacity(packageLength),
      magicBytes,
      Array(contentId),
      emptyPayloadLength,
      emptyPayloadChecksum)
  }
}
