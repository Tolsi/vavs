package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

case class Peer(ipPort: Int, port: Int)

object GetSignatures {
  final val ContentId: Byte = 0x02
}
case class Peers(peers: Seq[Peer]) extends NetworkMessage {
  override val contentId: Byte = GetSignatures.ContentId
}
