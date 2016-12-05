package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import java.net.InetSocketAddress

import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

object Peers {
  final val ContentId: Byte = 0x02
}
case class Peers(peers: Seq[InetSocketAddress]) extends NetworkMessage {
  override val contentId: Byte = Peers.ContentId
}
