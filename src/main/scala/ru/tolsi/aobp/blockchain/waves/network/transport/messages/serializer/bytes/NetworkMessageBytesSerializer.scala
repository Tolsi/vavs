package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage

object NetworkMessageBytesSerializer {
  val ChecksumLength = 4
}
abstract class NetworkMessageBytesSerializer[M <: NetworkMessage](implicit wbc: WavesBlockChain) extends BytesSerializer[M] {
  def calculateDataChecksum(data: Array[Byte]): Array[Byte] = {
    wbc.fastHash.hash(data).take(NetworkMessageBytesSerializer.ChecksumLength)
  }
}
