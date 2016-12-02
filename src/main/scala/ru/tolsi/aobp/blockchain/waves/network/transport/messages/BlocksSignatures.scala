package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.Signature64
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

object BlocksSignatures {
  final val ContentId: Byte = 0x15
}
case class BlocksSignatures(signatures: Seq[Signature64]) extends NetworkMessage {
  override val contentId: Byte = BlocksSignatures.ContentId
}
