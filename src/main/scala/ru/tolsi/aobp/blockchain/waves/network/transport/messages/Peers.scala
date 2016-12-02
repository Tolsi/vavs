package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

case class Peer(ipPort: Int, port: Int)

case class Peers(peers: Seq[Peer]) extends NetworkMessage {
  override def contentId: Byte = 0x02
}
