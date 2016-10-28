package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import scorex.crypto.encode.Base58
import scorex.crypto.signatures.Curve25519

object WavesPublicKeyAccount {
  def fromPublicKey(publicKey: Array[Byte])(implicit bc: WavesBlockChain): WavesPublicKeyAccount = {
    new WavesPublicKeyAccount(addressFromPublicKey(publicKey))
  }

  val AddressVersion: Byte = 1
  val ChecksumLength = 4
  val HashLength = 20
  val AddressLength = 1 + 1 + ChecksumLength + HashLength

  def addressFromPublicKey(publicKey: Array[Byte])(implicit bc: WavesBlockChain): String = {
    val publicKeyHash = ScorexHashChain.hash(publicKey).take(HashLength)
    val withoutChecksum = AddressVersion +: bc.chainId +: publicKeyHash
    Base58.encode(withoutChecksum ++ calcCheckSum(withoutChecksum))
  }

  private def calcCheckSum(withoutChecksum: Array[Byte]): Array[Byte] = ScorexHashChain.hash(withoutChecksum).take(ChecksumLength)
}

object WavesPrivateKeyAccount {
  def apply(seed: Array[Byte], keyPair: (Array[Byte], Array[Byte])) = WavesPrivateKeyAccount(seed, keyPair._1, keyPair._2)
  def apply(seed: Array[Byte]) = WavesPrivateKeyAccount(seed, Curve25519.createKeyPair(seed))
}

case class WavesPrivateKeyAccount(seed: Array[Byte], privateKey: Array[Byte], publicKey: Array[Byte])
  extends AccountWithAddress[WavesBlockChain](WavesPublicKeyAccount.addressFromPublicKey(publicKey))

case class WavesPublicKeyAccount(address: String) extends AccountWithAddress[WavesBlockChain](address)

trait WavesTransaction extends Transaction[WavesBlockChain] {
  val recipient: WavesPublicKeyAccount

  def timestamp: Long

  def amount: Long

  def fee: Long

  // todo good idea?
  def balanceChanges(): Seq[(WavesPublicKeyAccount, Long)]
}

trait WavesBlock extends Block[WavesBlockChain, WavesTransaction] {
  val version: Byte
  val timestamp: Long
  val reference: Signature64

  def transactions: Seq[Signed[WavesTransaction, Signature64]]

  val baseTarget: Long
  val generatorSignature: Signature32
}

class GenesisBlock(val version: Byte,
                   val timestamp: Long,
                   val reference: Signature64,
                   val transactions: Seq[Signed[WavesTransaction, Signature64]],
                   val baseTarget: Long,
                   val generatorSignature: Signature32) extends WavesBlock

abstract class WavesBlockChain extends BlockChain[WavesBlock, WavesTransaction] {
  val chainId: Byte

  override def txValidator: TxValidator = ???

  override def blockValidator: BlockValidator = ???
}
