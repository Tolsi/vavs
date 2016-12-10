package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Checkpoint

import scala.util.Try

class CheckpointDeserializer extends NetworkMessageBytesDeserializer[Checkpoint] {
  override def deserialize(array: Array[Byte]): Try[Checkpoint] = ???
}
