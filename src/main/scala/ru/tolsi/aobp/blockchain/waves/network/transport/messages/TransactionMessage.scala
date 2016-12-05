package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.SignedTransaction
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction

object TransactionMessage {
  final val ContentId: Byte = 0x19
}
case class TransactionMessage(tx: SignedTransaction[WavesTransaction]) extends NetworkMessage {
  override val contentId: Byte = TransactionMessage.ContentId
}
