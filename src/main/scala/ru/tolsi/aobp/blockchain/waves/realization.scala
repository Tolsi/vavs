package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import scorex.crypto.encode.Base58
import scorex.crypto.hash.Blake256
import scorex.crypto.signatures.Curve25519

import scala.util.Either

abstract class WavesBlockChain extends BlockChain {
  val chainId: Byte

  val secureHash = ScorexHashChain
  val fastHash = Blake256

  case class WavesAccount(override val publicKey: PublicKey, override val privateKey: Option[PrivateKey] = None)
    extends Account(publicKey, privateKey)

  case class WavesAddress(override val address: Array[Byte]) extends Address(address)

  object WavesAccount {
    def fromPublicKey(publicKey: PublicKey)(implicit bc: WavesBlockChain): WavesAccount = new WavesAccount(publicKey)

    private val AddressVersion: Byte = 1
    private val ChecksumLength = 4
    private val HashLength = 20
    private val AddressLength = 1 + 1 + ChecksumLength + HashLength

    def addressFromPublicKey(publicKey: Array[Byte])(implicit bc: WavesBlockChain): String = {
      val publicKeyHash = bc.secureHash.hash(publicKey).take(HashLength)
      val withoutChecksum = AddressVersion +: bc.chainId +: publicKeyHash
      Base58.encode(withoutChecksum ++ calcCheckSum(withoutChecksum))
    }

    private def calcCheckSum(withoutChecksum: Array[Byte]): Array[Byte] = ScorexHashChain.hash(withoutChecksum).take(ChecksumLength)

    def apply(keyPair: (PrivateKey, PublicKey))(implicit bc: WavesBlockChain): WavesAccount = this (keyPair._2, Some(keyPair._1))

    def apply(seed: Array[Byte])(implicit bc: WavesBlockChain): WavesAccount = this (Curve25519.createKeyPair(seed))
  }
  abstract class WavesTransaction extends Transaction {
    def id: Array[Byte]

    def typeId: Byte

    val recipient: Address

    def timestamp: Long

    def amount: Long

    def currency: WavesСurrency

    def fee: Long

    def feeCurrency: WavesСurrency

    // todo is it good idea? external implicit balance changes calculator
    //  def balanceChanges(): Seq[(WavesAccount, Long)]
  }

  trait SignedWavesTransaction extends WavesTransaction with SignedTransaction[Array[Byte]] {
    override def id: Array[Byte] = signature.value
  }

  trait AssetIssuanceTransaction extends SignedTransaction[Array[Byte]] {
    def issue: WavesMoney[Right[Waves.type, Asset]]

    def reissuable: Boolean
  }

  case class GenesisTransaction(recipient: Address, timestamp: Long, amount: Long) extends SignedWavesTransaction {
    override def typeId: Byte = 1

    override def fee: Long = 0

    override def currency: WavesСurrency = Waves

    override def feeCurrency: WavesСurrency = Waves

    override def signature: Signature[Array[Byte]] = ???
  }

  case class PaymentTransaction(sender: WavesAccount,
                                override val recipient: Address,
                                override val amount: Long,
                                override val fee: Long,
                                override val timestamp: Long) extends SignedWavesTransaction {
    override def typeId: Byte = 2

    override def currency: WavesСurrency = Waves

    override def feeCurrency: WavesСurrency = Waves

    override def signature: Signature[Array[Byte]] = ???
  }

  case class IssueTransaction(sender: WavesAccount,
                              name: Array[Byte],
                              description: Array[Byte],
                              issue: WavesMoney[Right[Waves.type, Asset]],
                              decimals: Byte,
                              reissuable: Boolean,
                              fee: WavesMoney[Left[Waves.type, Asset]],
                              timestamp: Long) extends AssetIssuanceTransaction {
    override def signature: Signature[Array[Byte]] = ???
  }

  case class ReissueTransaction(sender: WavesAccount,
                                issue: WavesMoney[Right[Waves.type, Asset]],
                                reissuable: Boolean,
                                fee: WavesMoney[Left[Waves.type, Asset]],
                                timestamp: Long,
                                signature: Signature[Array[Byte]]) extends AssetIssuanceTransaction

  case class TransferTransaction(timestamp: Long,
                                 sender: WavesAccount,
                                 recipient: Address,
                                 amount: WavesMoney[Either[Waves.type, Asset]],
                                 fee: WavesMoney[Either[Waves.type, Asset]],
                                 attachment: Array[Byte]) extends SignedTransaction[Array[Byte]] {
    override def signature: Signature[Array[Byte]] = ???
  }


  trait WavesBlock extends Block {
    override type Id = ArraySignature32

    val version: Byte
    val timestamp: Long
    val reference: ArraySignature64

    def transactions: Seq[Signed[WavesTransaction, Array[Byte], ArraySignature64]]

    val baseTarget: Long
    val generatorSignature: ArraySignature32
  }

  class GenesisBlock(val timestamp: Long,
                     val reference: ArraySignature64,
                     val transactions: Seq[Signed[WavesTransaction, Array[Byte], ArraySignature64]],
                     val baseTarget: Long,
                     val generatorSignature: ArraySignature32) extends WavesBlock {
    val version: Byte = 2
  }


  class SimpleBlock(val timestamp: Long,
                    val reference: ArraySignature64,
                    val transactions: Seq[Signed[WavesTransaction, Array[Byte], ArraySignature64]],
                    val baseTarget: Long,
                    val generatorSignature: ArraySignature32) extends WavesBlock {
    val version: Byte = 3
  }

  def state: StateStorage[this.type]

  def blocksStorage: BlockStorage[this.type]

  override def txValidator: TransactionValidator = ???

  override def blockValidator: BlockValidator = ???
}

abstract class WavesStateStorage extends StateStorage[WavesBlockChain] {
  type AssetId = String
  type BalanceAccount = String
  override type Balance = Long

  def currentState: Map[BalanceAccount, Balance]

  def currentBalance(balanceAccount: BalanceAccount): Balance
  def currentAssetBalance(balanceAccount: BalanceAccount, asset: AssetId): Balance

  def apply(b: WavesBlockChain#Block): Unit

  // todo tree storage
  def jumpToState(b: WavesBlockChain#Block): Unit
}
