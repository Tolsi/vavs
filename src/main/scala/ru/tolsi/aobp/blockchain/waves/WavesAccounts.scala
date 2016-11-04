package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.AbstractValidationError
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import scorex.crypto.signatures.Curve25519

private[waves] trait WavesAccounts {
  this: WavesBlockChain =>

  object Account {

    import Address._

    private[waves] val ChecksumLength = 4
    private[waves] val HashLength = 20

    private[waves] def calcCheckSum(withoutChecksum: Array[Byte]): Array[Byte] = ScorexHashChain.hash(withoutChecksum).take(ChecksumLength)

    def apply(keyPair: (PrivateKey, PublicKey)): Account = this (keyPair._2, Some(keyPair._1))

    def apply(seed: Array[Byte]): Account = this (Curve25519.createKeyPair(seed))

    def addressFromPublicKey(publicKey: Array[Byte]): Array[Byte] = {
      val publicKeyHash = secureHash.hash(publicKey).take(HashLength)
      val withoutChecksum = AddressVersion +: chainId +: publicKeyHash
      withoutChecksum ++ calcCheckSum(withoutChecksum)
    }
  }

  case class Account(override val publicKey: PublicKey, override val privateKey: Option[PrivateKey] = None)
    extends BlockChainAccount(publicKey, privateKey) {

    import Account._

    def address = Address(addressFromPublicKey(publicKey))
  }

  object Address {

    import Account._

    sealed abstract class AddressValidationError(m: => String) extends AbstractValidationError[Address](m)

    class WrongAddressVersion(message: => String) extends AddressValidationError(message)

    class WrongChainId(message: => String) extends AddressValidationError(message)

    class WrongAddressLength(message: => String) extends AddressValidationError(message)

    class WrongChecksum(message: => String) extends AddressValidationError(message)

    private[waves] val AddressVersion: Byte = 1
    private[waves] val AddressLength = 1 + 1 + ChecksumLength + HashLength

    def validateAddress(addressBytes: Array[Byte]): Option[AddressValidationError] = {
      val version = addressBytes.head
      val network = addressBytes.tail.head
      if (version != AddressVersion) {
        Some(new WrongAddressVersion(s"$version != $AddressVersion"))
      } else if (network != chainId) {
        Some(new WrongChainId(s"$network != $chainId"))
      } else if (addressBytes.length != AddressLength) {
        Some(new WrongAddressLength(s"${addressBytes.length} != $AddressLength"))
      } else {
        val checkSum = addressBytes.takeRight(ChecksumLength)
        val checkSumGenerated = calcCheckSum(addressBytes.dropRight(ChecksumLength))
        if (checkSum.sameElements(checkSumGenerated)) {
          None
        } else {
          Some(new WrongChecksum(s"$checkSumGenerated != $checkSum"))
        }
      }
    }
  }

  case class Address(override val address: Array[Byte]) extends BlockChainAddress(address) {

    import Address._

    // todo move to validator
    def validate: Option[AddressValidationError] = validateAddress(address)
  }

}