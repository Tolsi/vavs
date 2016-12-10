package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.waves.network.transport.messages.GetBlock

import scala.util.Try

class GetBlockDeserializer extends NetworkMessageBytesDeserializer[GetBlock] {
  override def deserialize(array: Array[Byte]): Try[GetBlock] = ???
}
