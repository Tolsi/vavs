package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

case class GetPeers() extends NetworkMessage {
  override def contentId: Byte = 0x01
}
