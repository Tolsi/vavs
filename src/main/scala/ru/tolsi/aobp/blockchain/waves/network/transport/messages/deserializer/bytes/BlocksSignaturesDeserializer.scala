package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.waves.network.transport.messages.BlocksSignatures

import scala.util.Try

class BlocksSignaturesDeserializer extends NetworkMessageBytesDeserializer[BlocksSignatures] {
  override def deserialize(array: Array[Byte]): Try[BlocksSignatures] = ???
}
