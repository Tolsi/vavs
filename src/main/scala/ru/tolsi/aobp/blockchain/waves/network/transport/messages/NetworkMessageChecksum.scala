package ru.tolsi.aobp.blockchain.waves.network.transport.messages

import scorex.crypto.encode.Base58
import scorex.crypto.hash.Blake256

import scala.util.{Failure, Success, Try}

object NetworkMessageChecksum {
  val ChecksumLength = 4
}

case class InvalidDataChecksum(message: String) extends RuntimeException(message)

trait NetworkMessageChecksum {
  def calculateDataChecksum(data: Array[Byte]): Array[Byte] = {
    Blake256.hash(data).take(NetworkMessageChecksum.ChecksumLength)
  }

  def validateDataChecksum(data: Array[Byte], checksum: Array[Byte]): Try[Array[Byte]] = {
    val calculated = calculateDataChecksum(data)
    if (calculated sameElements checksum) {
      Success(data)
    } else {
      Failure(InvalidDataChecksum(s"Invalid data checksum: ${Base58.encode(calculated)} != ${Base58.encode(checksum)}"))
    }
  }
}
