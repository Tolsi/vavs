package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.Signature64
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

object GetSignatures {
  final val ContentId: Byte = 0x14
}
case class GetSignatures(signatures: Seq[Signature64]) extends NetworkMessage {
  override val contentId: Byte = GetSignatures.ContentId
}
