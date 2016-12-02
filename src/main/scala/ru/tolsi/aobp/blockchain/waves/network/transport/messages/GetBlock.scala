package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.Signature64
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

object GetBlock {
  final val ContentId: Byte = 0x16
}
case class GetBlock(blockSignature: Signature64) extends NetworkMessage {
  override val contentId: Byte = 0x16
}
