package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{Block => BaseBlock, _}
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import scorex.crypto.encode.Base58
import scorex.crypto.hash.Blake256
import scorex.crypto.signatures.Curve25519

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

trait WavesTransaction extends Transaction[WavesBlockChain] {
  val recipient: AccountWithAddress[WavesBlockChain]

  def timestamp: Long

  def amount: Long

  def fee: Long

  // todo is it good idea? external implicit balance changes calculator
  def balanceChanges(): Seq[(WavesAccount, Long)]
}


trait WavesBlock extends BaseBlock[WavesBlockChain, WavesTransaction] {
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

abstract class WavesStateStorage extends StateStorage[WavesTransaction, WavesBlock, WavesStateStorage#BalanceAccount] {
  type BalanceAccount = String
  type AssetId = String
  type Balance = Long

  def currentState: Map[BalanceAccount, Balance]

  def currentBalance(balanceAccount: BalanceAccount): Balance
  def currentAssetBalance(balanceAccount: BalanceAccount, asset: AssetId): Balance

  def apply(b: WavesBlock): Unit

  def rollback(b: WavesBlock): Unit
}
