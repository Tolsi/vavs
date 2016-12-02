package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.SignedBlock
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

case class Block(block: SignedBlock[WavesBlock]) extends NetworkMessage {
  override def contentId: Byte = 0x17
}
