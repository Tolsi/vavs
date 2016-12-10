package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.waves.network.transport.messages.GetSignatures

import scala.util.Try

class GetBlocksSignaturesSerializer extends NetworkMessageBytesDeserializer[GetSignatures] {
  override def deserialize(array: Array[Byte]): Try[GetSignatures] = ???
}
