package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{Block => BaseBlock, _}
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import scorex.crypto.encode.Base58
import scorex.crypto.hash.Blake256
import scorex.crypto.signatures.Curve25519

import scala.util.Either

object WavesAccount {

  def fromPublicKey(publicKey: PublicKey)(implicit bc: WavesBlockChain): WavesAccount =
    new WavesAccount(publicKey)

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

  def apply(keyPair: (PrivateKey, PublicKey))(implicit bc: WavesBlockChain): WavesAccount = this(keyPair._2, Some(keyPair._1))
  def apply(seed: Array[Byte])(implicit bc: WavesBlockChain): WavesAccount = this(Curve25519.createKeyPair(seed))
}

case class WavesAccount(publicKey: PublicKey, privateKey: Option[PrivateKey] = None)(implicit bc: WavesBlockChain)
  extends AccountWithAddress(WavesAccount.addressFromPublicKey(publicKey)(bc))

abstract class WavesTransaction extends Transaction[WavesBlockChain] {
  def id: Array[Byte]
  def typeId: Byte
  val recipient: AccountWithAddress[WavesBlockChain]
  def timestamp: Long
  def amount: Long
  def currency: WavesСurrency
  def fee: Long
  def feeCurrency: WavesСurrency
  // todo is it good idea? external implicit balance changes calculator
//  def balanceChanges(): Seq[(WavesAccount, Long)]
}

trait SignedWavesTransaction extends WavesTransaction with SignedTransaction[WavesTransaction, WavesBlockChain, Array[Byte]] {
  override def id: Array[Byte] = signature.value
}

trait AssetIssuanceTransaction extends SignedTransaction[WavesTransaction, WavesBlockChain, Array[Byte]] {
  def issue: WavesMoney[Right[Waves.type, Asset]]
  def reissuable: Boolean
}

case class GenesisTransaction(recipient: AccountWithAddress[WavesBlockChain], timestamp: Long, amount: Long) extends SignedWavesTransaction {
  override def typeId: Byte = 1
  override def fee: Long = 0
  override def currency: WavesСurrency = Waves
  override def feeCurrency: WavesСurrency = Waves
  override def signature: Signature[Array[Byte]] = ???
}

case class PaymentTransaction(sender: WavesAccount,
                         override val recipient: AccountWithAddress[WavesBlockChain],
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
                              signature: Array[Byte])  extends AssetIssuanceTransaction

case class TransferTransaction(timestamp: Long,
                          sender: WavesAccount,
                          recipient: AccountWithAddress[WavesBlockChain],
                          amount: WavesMoney[Either[Waves.type, Asset]],
                          fee: WavesMoney[Either[Waves.type, Asset]],
                          attachment: Array[Byte]) extends SignedTransaction[WavesTransaction, WavesBlockChain, Array[Byte]] {
  override def signature: Signature[Array[Byte]] = ???
}


trait WavesBlock extends BaseBlock[WavesTransaction, WavesBlock] {
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


class Block(val timestamp: Long,
                   val reference: ArraySignature64,
                   val transactions: Seq[Signed[WavesTransaction, Array[Byte], ArraySignature64]],
                   val baseTarget: Long,
                   val generatorSignature: ArraySignature32) extends WavesBlock {
  val version: Byte = 3
}

abstract class WavesBlockChain extends BlockChain[WavesBlock, WavesTransaction] {
  val chainId: Byte

  val secureHash = ScorexHashChain
  val fastHash = Blake256

  def state: StateStorage[WavesTransaction, WavesBlock, this.type]

  def blocksStorage: BlockStorage[WavesTransaction, WavesBlock, this.type]

  override def txValidator: TxValidator = ???

  override def blockValidator: BlockValidator = ???
}

abstract class WavesStateStorage extends StateStorage[WavesTransaction, WavesBlock, WavesBlockChain] {
  type AssetId = String
  type BalanceAccount = String
  override type Balance = Long

  def currentState: Map[BalanceAccount, Balance]

  def currentBalance(balanceAccount: BalanceAccount): Balance
  def currentAssetBalance(balanceAccount: BalanceAccount, asset: AssetId): Balance

  def apply(b: WavesBlock): Unit

  // todo tree storage
  def jumpToState(b: WavesBlock): Unit
}
