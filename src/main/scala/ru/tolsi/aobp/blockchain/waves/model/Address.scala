package ru.tolsi.aobp.blockchain.waves.model

import cats.data.Validated._
import ru.tolsi.aobp.blockchain.waves.crypto.SecureHashChain
import ru.tolsi.aobp.blockchain.waves.model.Address._
import scorex.crypto.encode.Base58

sealed trait Address {

  def publicKey: PublicKey
  def version: Version
  def chainId: ChainId
}

object Address {

  type PublicKey = Array[Byte]
  type Version   = Byte
  type ChainId   = Byte

  private case class AddressImpl(version: Version, chainId: ChainId, publicKey: PublicKey) extends Address

  val ChecksumLength = 4
  val PkLength       = 20

  def apply(bytes: Array[Byte]): ValidationResult[Address] = {
    if (bytes.length != 26) {
      invalidNel(s"Invalid length: expected: 26 actual: ${bytes.length}")
    } else {
      val checksum     = bytes.takeRight(ChecksumLength)
      val dropChecksum = bytes.dropRight(ChecksumLength)
      val expected = SecureHashChain.hash(dropChecksum).take(ChecksumLength)
      if (expected sameElements checksum) {
        val ver     = bytes(0)
        val chainId = bytes(1)
        val pkHash  = bytes.slice(2, 2 + PkLength)
        valid(AddressImpl(ver, chainId, pkHash))
      } else {
        invalidNel(s"Invalid checksum: expected: ${Base58.encode(expected)}")
      }
    }
  }
}
