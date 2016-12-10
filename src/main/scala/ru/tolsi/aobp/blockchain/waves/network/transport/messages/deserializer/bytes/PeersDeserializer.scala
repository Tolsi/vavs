package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Peers

import scala.util.Try

class PeersDeserializer extends NetworkMessageBytesDeserializer[Peers] {
  override def deserialize(array: Array[Byte]): Try[Peers] = ???
}
