package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.SignedTransaction
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction

object Transaction {
  final val ContentId: Byte = 0x19
}
case class Transaction(tx: SignedTransaction[WavesTransaction]) extends NetworkMessage {
  override val contentId: Byte = Transaction.ContentId
}
