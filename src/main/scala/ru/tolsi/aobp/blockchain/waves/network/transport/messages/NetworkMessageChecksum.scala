package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import scorex.crypto.hash.Blake256

object NetworkMessageChecksum {
  val ChecksumLength = 4
}

trait NetworkMessageChecksum {
  def calculateDataChecksum(data: Array[Byte]): Array[Byte] = {
    Blake256.hash(data).take(NetworkMessageChecksum.ChecksumLength)
  }
}
