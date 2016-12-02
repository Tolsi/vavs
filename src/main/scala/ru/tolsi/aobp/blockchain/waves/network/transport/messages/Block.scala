package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.SignedBlock
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

object Block {
  final val ContentId: Byte = 0x17
}
case class Block(block: SignedBlock[WavesBlock]) extends NetworkMessage {
  override val contentId: Byte = Block.ContentId
}
