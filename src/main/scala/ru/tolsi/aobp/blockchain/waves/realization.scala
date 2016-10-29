package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import scorex.crypto.encode.Base58
import scorex.crypto.hash.Blake256
import scorex.crypto.signatures.Curve25519

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

    def apply(keyPair: (PrivateKey, PublicKey)): Account = this (keyPair._2, Some(keyPair._1))

    def apply(seed: Array[Byte]): Account = this (Curve25519.createKeyPair(seed))

    def addressFromPublicKey(publicKey: Array[Byte]): Array[Byte] = {
      val publicKeyHash = secureHash.hash(publicKey).take(HashLength)
      val withoutChecksum = AddressVersion +: chainId +: publicKeyHash
      withoutChecksum ++ calcCheckSum(withoutChecksum)
    }

    def isValidAddress(addressBytes: Array[Byte]): Boolean = {
        val version = addressBytes.head
        val network = addressBytes.tail.head
        if (version != AddressVersion) {
          // todo validation error
//          log.warn(s"Unknown address version: $version")
          false
        } else if (network != chainId) {
          // todo validation error
//          log.warn(s"Unknown network: $network")
          false
        } else {
          if (addressBytes.length != Account.AddressLength){
            false
          } else {
            val checkSum = addressBytes.takeRight(ChecksumLength)

            val checkSumGenerated = calcCheckSum(addressBytes.dropRight(ChecksumLength))

            checkSum.sameElements(checkSumGenerated)
            // todo checksum validation error
          }
        }
      }
  }

  case class Account(override val publicKey: PublicKey, override val privateKey: Option[PrivateKey] = None)
    extends BlockChainAccount(publicKey, privateKey) {

    import Account._

    def address = Address(addressFromPublicKey(publicKey))

    def isValid = isValidAddress(address.address)
  }

  case class Address(override val address: Array[Byte]) extends BlockChainAddress(address)

}

private[waves] trait WavesTransactionsSigners {
  this: WavesTransactions =>

}

private[waves] trait WavesTransactions {
  this: WavesBlockChain =>

  object TransactionType extends Enumeration {
    val GenesisTransaction = Value(1)
    val PaymentTransaction = Value(2)
    val IssueTransaction = Value(3)
    val TransferTransaction = Value(4)
    val ReissueTransaction = Value(5)
  }

  abstract class Transaction extends BlockChainTransaction {
    def id: Array[Byte]

    def typeId: TransactionType.Value

    val recipient: Address

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

  case class GenesisTransaction(recipient: Address, timestamp: Long, amount: Long, signature: Signature[Array[Byte]]) extends SignedTransaction {
    override val typeId = TransactionType.GenesisTransaction

    override val fee: Long = 0

    override val currency: WavesСurrency = Waves

    override val feeCurrency: WavesСurrency = Waves
  }

  case class PaymentTransaction(sender: Account,
                                override val recipient: Address,
                                override val amount: Long,
                                override val fee: Long,
                                override val timestamp: Long,
                                signature: Signature[Array[Byte]]) extends SignedTransaction {
    override def typeId = TransactionType.PaymentTransaction

    override def currency: WavesСurrency = Waves

    override def feeCurrency: WavesСurrency = Waves
  }

  trait AssetIssuanceTransaction extends SignedTransaction {
    def issue: WavesMoney[Right[Waves.type, Asset]]
    def reissuable: Boolean
  }

  case class IssueTransaction(sender: Account,
                              name: Array[Byte],
                              description: Array[Byte],
                              issue: WavesMoney[Right[Waves.type, Asset]],
                              decimals: Byte,
                              reissuable: Boolean,
                              feeMoney: WavesMoney[Left[Waves.type, Asset]],
                              timestamp: Long,
                              signature: Signature[Array[Byte]]) extends AssetIssuanceTransaction {
    override def typeId = TransactionType.IssueTransaction

    override val recipient: Address = sender.address

    override def amount: Long = issue.value

    override def currency: WavesСurrency = issue.currency.b

    override def feeCurrency: WavesСurrency = feeMoney.currency.a

    override def fee: Long = feeMoney.value
  }

  case class ReissueTransaction(sender: Account,
                                issue: WavesMoney[Right[Waves.type, Asset]],
                                reissuable: Boolean,
                                feeMoney: WavesMoney[Left[Waves.type, Asset]],
                                timestamp: Long,
                                signature: Signature[Array[Byte]]) extends AssetIssuanceTransaction {
    override def typeId = TransactionType.ReissueTransaction

    override val recipient: Address = sender.address

    override def amount: Long = issue.value

    override def currency: WavesСurrency = issue.currency.b

    override def feeCurrency: WavesСurrency = feeMoney.currency.a

    override def fee: Long = feeMoney.value
  }

  case class TransferTransaction(timestamp: Long,
                                 sender: Account,
                                 recipient: Address,
                                 transfer: WavesMoney[Either[Waves.type, Asset]],
                                 feeMoney: WavesMoney[Either[Waves.type, Asset]],
                                 attachment: Array[Byte],
                                 signature: Signature[Array[Byte]]) extends SignedTransaction {
    override def typeId = TransactionType.TransferTransaction

    override def amount: Long = transfer.value

    override def currency: WavesСurrency = transfer.currency.fold(identity, identity)

    override def feeCurrency: WavesСurrency = feeMoney.currency.fold(identity, identity)

    override def fee: Long = feeMoney.value
  }

}

trait WavesTransactionsValidators {
  self: WavesBlockChain =>

  class ReissueTransactionValidator extends TransactionValidator[ReissueTransaction] {
    override def validate(tx: ReissueTransaction)(implicit blockChain: self.type):
    Either[Seq[TransactionValidationError[ReissueTransaction]], ReissueTransaction] = {
//      if (tx.sender.isValid) {
        // todo adress validation error
//        ???
//      } else if (quantity <= 0) {
//        ValidationResult.NegativeAmount
//      } else if (fee <= 0) {
//        ValidationResult.InsufficientFee
//      } else if (!EllipticCurveImpl.verify(tx.signature, tx.toSign, tx.sender.publicKey)) {
//        ValidationResult.InvalidSignature
//      } else ValidationResult.ValidateOke
      ???
    }
  }
  override def txValidator: BlockChainTransactionValidator =
    new AggregatedValidatorOnBlockchain[self.type, T, TransactionValidationError[_ <: T]](Seq[TransactionValidator[_ <: T]](
      new ReissueTransactionValidator
  ))
}

trait WavesBlocksValidators {
  self: WavesBlockChain =>

  override def blockValidator: BlockChainBlockValidator = ???
}

private[waves] abstract class WavesBlockChain extends BlockChain
  with WavesTransactions
  with WavesTransactionsSigners
  with WavesTransactionsValidators
  with WavesAccounts
  with WavesBlocks
  with WavesBlocksValidators {
  def chainId: Byte

  final type T = Transaction
  final type B = Block
  final type AС = Account
  final type AВ = Address

  final val secureHash = ScorexHashChain
  final val fastHash = Blake256

  def state: StateStorage[this.type]

  def blocksStorage: BlockStorage[this.type]
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
