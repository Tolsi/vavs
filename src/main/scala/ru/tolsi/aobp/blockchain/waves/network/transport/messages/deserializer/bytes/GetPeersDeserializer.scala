package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.waves.network.transport.messages.GetPeers

import scala.util.Try

class GetPeersDeserializer extends NetworkMessageBytesDeserializer[GetPeers] {
  override def deserialize(array: Array[Byte]): Try[GetPeers] = ???
}
