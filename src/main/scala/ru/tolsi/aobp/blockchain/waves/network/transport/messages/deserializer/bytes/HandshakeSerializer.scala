package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Handshake

import scala.util.Try

class HandshakeSerializer extends NetworkMessageBytesDeserializer[Handshake] {
  override def deserialize(array: Array[Byte]): Try[Handshake] = ???
}
