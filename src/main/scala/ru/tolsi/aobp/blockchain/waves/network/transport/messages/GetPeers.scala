package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

object GetPeers {
  final val ContentId: Byte = 0x01
}
case class GetPeers() extends NetworkMessage {
  override val contentId: Byte = GetPeers.ContentId
}
