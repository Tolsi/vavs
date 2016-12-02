package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

object Score {
  final val ContentId: Byte = 0x18
}
case class Score(score: BigInt) extends NetworkMessage {
  override val contentId: Byte = Score.ContentId
}
