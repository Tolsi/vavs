package ru.tolsi.aobp.blockchain.waves.network.transport

object NetworkMessage {
  val MagicBytes: Array[Byte] = Array[Byte](0x12, 0x34, 0x56, 0x78)
}
trait NetworkMessage {
  def contentId: Byte
}
