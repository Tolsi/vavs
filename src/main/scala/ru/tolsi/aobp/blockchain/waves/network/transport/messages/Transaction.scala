package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.SignedTransaction
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction

case class Transaction(tx: SignedTransaction[WavesTransaction]) extends NetworkMessage {
  override def contentId: Byte = 0x19
}
