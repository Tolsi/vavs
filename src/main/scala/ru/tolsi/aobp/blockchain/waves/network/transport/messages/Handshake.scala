package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

case class ApplicationVersion(major: Int, minor: Int, patch: Int)

case class Handshake(applicationName: String, applicationVersion: ApplicationVersion, nodeName: String, nonce: Long, declaredAddress: Option[String], timestamp: Long) extends NetworkMessage {
  override val contentId: Byte = ???
}
