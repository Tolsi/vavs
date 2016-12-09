package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.primitives.{Bytes, Ints}
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer._
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Peers

class PeersSerializer extends NetworkMessageSerializer[Peers] {
  override def serialize(peers: Peers): Array[Byte] = {
    val magicBytes = NetworkMessage.MagicBytes
    val contentId = peers.contentId

    val payload = Bytes.concat(Ints.toByteArray(peers.peers.size), peers.peers.foldLeft(Array.empty[Byte]) { case (bs, peer) =>
      Bytes.concat(bs, peer.getAddress.getAddress, intBytesEnsureCapacity(peer.getPort))
    })

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
