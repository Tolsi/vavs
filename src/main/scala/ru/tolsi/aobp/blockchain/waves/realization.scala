package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import scorex.crypto.hash.Blake256
import scorex.crypto.signatures.Curve25519

import scala.util.Either

private[waves] trait WavesBlocks {
  this: WavesBlockChain =>

  trait WavesBlock extends BlockChainBlock {
    override type Id = ArraySignature32

    val version: Byte
    val timestamp: Long
    val reference: ArraySignature64

    def transactions: Seq[Signed[Transaction, Array[Byte], ArraySignature64]]

    val baseTarget: Long
    val generatorSignature: ArraySignature32
  }

  class GenesisBlock(val timestamp: Long,
                     val reference: ArraySignature64,
                     val transactions: Seq[Signed[Transaction, Array[Byte], ArraySignature64]],
                     val baseTarget: Long,
                     val generatorSignature: ArraySignature32) extends WavesBlock {
    val version: Byte = 2
  }


  class Block(val timestamp: Long,
              val reference: ArraySignature64,
              val transactions: Seq[Signed[Transaction, Array[Byte], ArraySignature64]],
              val baseTarget: Long,
              val generatorSignature: ArraySignature32) extends WavesBlock {
    val version: Byte = 3
  }

}

private[waves] trait WavesAccounts {
  this: WavesBlockChain =>

  object Account {
    private val AddressVersion: Byte = 1
    private val ChecksumLength = 4
    private val HashLength = 20
    private val AddressLength = 1 + 1 + ChecksumLength + HashLength

    private def calcCheckSum(withoutChecksum: Array[Byte]): Array[Byte] = ScorexHashChain.hash(withoutChecksum).take(ChecksumLength)

    def apply(keyPair: (PrivateKey, PublicKey))(implicit bc: WavesBlockChain): Account = this (keyPair._2, Some(keyPair._1))

    def apply(seed: Array[Byte])(implicit bc: WavesBlockChain): Account = this (Curve25519.createKeyPair(seed))

    def addressFromPublicKey(publicKey: Array[Byte]): Array[Byte] = {
      val publicKeyHash = secureHash.hash(publicKey).take(HashLength)
      val withoutChecksum = AddressVersion +: chainId +: publicKeyHash
      withoutChecksum ++ calcCheckSum(withoutChecksum)
    }
  }

  case class Account(override val publicKey: PublicKey, override val privateKey: Option[PrivateKey] = None)
    extends BlockChainAccount(publicKey, privateKey) {

    import Account.addressFromPublicKey

    def address = Address(addressFromPublicKey(publicKey))
  }

  case class Address(override val address: Array[Byte]) extends BlockChainAddress(address)

}

private[waves] trait WavesTransactions {
  this: WavesBlockChain =>

  abstract class Transaction extends BlockChainTransaction {
    def id: Array[Byte]

    def typeId: Byte

    val recipient: BlockChainAddress

    def timestamp: Long

    def amount: Long

    def currency: WavesСurrency

    def fee: Long

    def feeCurrency: WavesСurrency

    // todo is it good idea? external implicit balance changes calculator
    //  def balanceChanges(): Seq[(WavesAccount, Long)]
  }

  trait SignedTransaction extends Transaction with BlockChainSignedTransaction[Array[Byte]] {
    override def id: Array[Byte] = signature.value
  }

  trait AssetIssuanceTransaction extends BlockChainSignedTransaction[Array[Byte]] {
    def issue: WavesMoney[Right[Waves.type, Asset]]
    def reissuable: Boolean
  }

  case class GenesisTransaction(recipient: BlockChainAddress, timestamp: Long, amount: Long) extends SignedTransaction {
    override val typeId: Byte = 1

    override val fee: Long = 0

    override val currency: WavesСurrency = Waves

    override val feeCurrency: WavesСurrency = Waves

    override val signature: Signature[Array[Byte]] = ???
  }

  case class PaymentTransaction(sender: Account,
                                override val recipient: BlockChainAddress,
                                override val amount: Long,
                                override val fee: Long,
                                override val timestamp: Long) extends SignedTransaction {
    override def typeId: Byte = 2

    override def currency: WavesСurrency = Waves

    override def feeCurrency: WavesСurrency = Waves

    override def signature: Signature[Array[Byte]] = ???
  }

  case class IssueTransaction(sender: Account,
                              name: Array[Byte],
                              description: Array[Byte],
                              issue: WavesMoney[Right[Waves.type, Asset]],
                              decimals: Byte,
                              reissuable: Boolean,
                              fee: WavesMoney[Left[Waves.type, Asset]],
                              timestamp: Long) extends AssetIssuanceTransaction {
    override def signature: Signature[Array[Byte]] = ???
  }

  case class ReissueTransaction(sender: Account,
                                issue: WavesMoney[Right[Waves.type, Asset]],
                                reissuable: Boolean,
                                fee: WavesMoney[Left[Waves.type, Asset]],
                                timestamp: Long,
                                signature: Signature[Array[Byte]]) extends AssetIssuanceTransaction

  case class TransferTransaction(timestamp: Long,
                                 sender: Account,
                                 recipient: BlockChainAddress,
                                 amount: WavesMoney[Either[Waves.type, Asset]],
                                 fee: WavesMoney[Either[Waves.type, Asset]],
                                 attachment: Array[Byte]) extends BlockChainSignedTransaction[Array[Byte]] {
    override def signature: Signature[Array[Byte]] = ???
  }

}


private[waves] abstract class WavesBlockChain extends BlockChain
  with WavesTransactions
  with WavesAccounts
  with WavesBlocks {
  def chainId: Byte

  final type T = Transaction
  final type B = Block
  final type AС = Account
  final type AВ = Address

  final val secureHash = ScorexHashChain
  final val fastHash = Blake256

  def state: StateStorage[this.type]

  def blocksStorage: BlockStorage[this.type]

  override def txValidator: TransactionValidator = ???

  override def blockValidator: BlockValidator = ???
}

private[waves] abstract class WavesStateStorage extends StateStorage[WavesBlockChain] {
  type AssetId = String
  type BalanceAccount = String
  override type Balance = Long

  def currentState: Map[BalanceAccount, Balance]

  def currentBalance(balanceAccount: BalanceAccount): Balance
  def currentAssetBalance(balanceAccount: BalanceAccount, asset: AssetId): Balance

  def apply(b: WavesBlockChain#BlockChainBlock): Unit

  // todo tree storage
  def jumpToState(b: WavesBlockChain#BlockChainBlock): Unit
}
