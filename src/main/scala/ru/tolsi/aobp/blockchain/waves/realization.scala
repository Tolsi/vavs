package ru.tolsi.aobp.blockchain.waves

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.crypto.{ScorexHashChain, Signature64}
import scorex.crypto.hash.Blake256
import scorex.crypto.signatures.Curve25519

import scala.util.Try

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

private[waves] trait WavesTransactionsSigners {
  this: WavesBlockChain =>
  object GenesisTransactionSigner extends Signer[WavesBlockChain, GenesisTransaction, Array[Byte], ArraySignature64] {
    val TypeLength = 1
    val TimestampLength = 8
    val AmountLength = 8

    override def sign(tx: GenesisTransaction)(implicit blockChain: WavesBlockChain): Signed[GenesisTransaction, Array[Byte], ArraySignature64] = {
      val typeBytes = Bytes.ensureCapacity(Ints.toByteArray(TransactionType.GenesisTransaction.id), TypeLength, 0)
      val timestampBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.timestamp), TimestampLength, 0)
      val amountBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.amount), AmountLength, 0)
      val data = Bytes.concat(typeBytes, timestampBytes, tx.recipient.address, amountBytes)

      val h = blockChain.fastHash(data)
      val sing = Bytes.concat(h, h)
      new SignedTransaction[GenesisTransaction](tx) {
        override def signature: ArraySignature64 = Signature64(sing)
      }
    }
  }
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

  abstract class SignedTransaction[TX <: T](tx: TX) extends Transaction with BlockChainSignedTransaction[TX, Array[Byte], ArraySignature64]  {
    override def signed: TX = tx

    override def typeId: TransactionType.Value = tx.typeId

    override val recipient: Address = tx.recipient

    override def timestamp: Long = tx.timestamp

    override def amount: Long = tx.amount

    override def currency: WavesСurrency = tx.currency

    override def fee: Long = tx.fee

    override def feeCurrency: WavesСurrency = tx.feeCurrency
  }

  case class GenesisTransaction(recipient: Address, timestamp: Long, amount: Long) extends Transaction {
    override val typeId = TransactionType.GenesisTransaction

    override val fee: Long = 0

    override val currency: WavesСurrency = Waves

    override val feeCurrency: WavesСurrency = Waves
  }

  case class PaymentTransaction(sender: Account,
                                override val recipient: Address,
                                override val amount: Long,
                                override val fee: Long,
                                override val timestamp: Long) extends Transaction {
    override def typeId = TransactionType.PaymentTransaction

    override def currency: WavesСurrency = Waves

    override def feeCurrency: WavesСurrency = Waves
  }

  trait AssetIssuanceTransaction extends Transaction {
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
                              timestamp: Long) extends AssetIssuanceTransaction {
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
                                timestamp: Long) extends AssetIssuanceTransaction {
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
                                 attachment: Array[Byte]) extends Transaction {
    override def typeId = TransactionType.TransferTransaction

    override def amount: Long = transfer.value

    override def currency: WavesСurrency = transfer.currency.fold(identity, identity)

    override def feeCurrency: WavesСurrency = feeMoney.currency.fold(identity, identity)

    override def fee: Long = feeMoney.value
  }

}

trait WavesTransactionsValidators {
  self: WavesBlockChain =>

  abstract class AbstractTransactionValidator[TX <: T] extends TransactionValidator[TX] {
    private[waves] val MaxAttachmentSize = 140

    private[waves] def addressValidation(address: Address): Option[WrongAddress] = {
      address.validate.map(error => new WrongAddress(error.message))
    }

    private[waves] def attachmentSizeValidation(attachment: Array[Byte]): Option[WrongAttachmentSize] = {
      if (attachment.length > MaxAttachmentSize) {
        Some(new WrongAttachmentSize(s"${attachment.length} > $MaxAttachmentSize"))
      } else None
    }

    private[waves] def negativeAmountValidation(amount: Long): Option[WrongAmount] = {
      if (amount <= 0) {
        Some(new WrongAmount(s"$amount <= 0"))
      } else None
    }

    private[waves] def negativeFeeValidation(fee: Long): Option[WrongFee] = {
      if (fee <= 0) {
        Some(new WrongFee(s"$fee <= 0"))
      } else None
    }

    private[waves] def overflowValidation(amount: WavesMoney[_ <: Either[Waves.type, Asset]],
                                          fee: WavesMoney[_ <: Either[Waves.type, Asset]]): Option[Overflow] = {
      if (amount.currency == fee.currency &&
        Try(Math.addExact(amount.value, fee.value)).isFailure) {
        Some(new Overflow(s"${amount.currency}: $amount + $fee = ${amount.value + fee.value}"))
      } else None
    }

    private[waves] def overflowValidation(amount: Long,
                                          fee: Long): Option[Overflow] = {
      if (Try(Math.addExact(amount, fee)).isFailure) {
        Some(new Overflow(s"$amount + $fee = ${amount + fee}"))
      } else None
    }
  }

  abstract class AbstractSignedTransactionValidator[TX <: T, STX <: ST[TX]](
                                                                             signer: Signer[WavesBlockChain, TX, Array[Byte], ArraySignature64],
                                                                             txValidator: AbstractTransactionValidator[TX]
                                                                           ) extends SignedTransactionValidator[TX, STX] {
    private[waves] def signatureValidation(tx: STX, signature: Signature[Array[Byte]]): Option[WrongSignature] = {
      // todo rewrite
      if (signer.sign(tx.signed)(self).signature.value sameElements tx.signature.value) {
        Some(new WrongSignature(s"Signature is not valid"))
      } else None
    }

    override def validate(stx: STX)(implicit blockChain: WavesTransactionsValidators.this.type): Either[Seq[TransactionValidationError[STX]], STX] = {
      txValidator.validate(stx.signed) match {
        case Left(errors) =>
          // todo it works?
          Left(errors.map(_.asInstanceOf[TransactionValidationError[STX]]))
        case Right(_) =>
          val signatureError = signatureValidation(stx, stx.signature)
          if (signatureError.isDefined) {
            Left[Seq[TransactionValidationError[STX]], STX](Seq[TransactionValidationError[STX]](signatureError.get))
          } else {
            Right[Seq[TransactionValidationError[STX]], STX](stx)
          }
      }
    }
  }

  object GenesisTransactionValidator extends AbstractTransactionValidator[GenesisTransaction] {
    override def validate(tx: GenesisTransaction)(implicit blockChain: WavesTransactionsValidators.this.type): Either[Seq[TransactionValidationError[GenesisTransaction]], GenesisTransaction] = {
      val errors = Seq(
        addressValidation(tx.recipient),
        negativeAmountValidation(tx.amount)
      ).flatten
      if (errors.nonEmpty) Left(errors) else Right(tx)
    }
  }

  object PaymentTransactionValidator extends AbstractTransactionValidator[PaymentTransaction] {
    override def validate(tx: PaymentTransaction)(implicit blockChain: WavesTransactionsValidators.this.type): Either[Seq[TransactionValidationError[PaymentTransaction]], PaymentTransaction] = {
      val errors = Seq(
        addressValidation(tx.recipient),
        negativeAmountValidation(tx.amount),
        negativeFeeValidation(tx.fee),
        overflowValidation(tx.amount, tx.fee)
      ).flatten
      if (errors.nonEmpty) Left(errors) else Right(tx)
    }
  }

  object IssueTransactionValidator extends AbstractTransactionValidator[IssueTransaction] {
    val MaxDescriptionLength = 1000
    val MaxAssetNameLength = 16
    val MinAssetNameLength = 4
    val MinFee = 100000000
    val MaxDecimals = 8

    private[waves] def smallFeeValidation(fee: Long): Option[WrongFee] = {
      if (fee < MinFee) {
        Some(new WrongFee(s"$fee < $MinFee"))
      } else None
    }

    private[waves] def maxAssetNameLength(assetName: Array[Byte]): Option[WrongAssetName] = {
      if (assetName.length > MaxAssetNameLength) {
        Some(new WrongAssetName(s"${assetName.length} > $MaxAssetNameLength"))
      } else None
    }

    private[waves] def minAssetNameLength(assetName: Array[Byte]): Option[WrongAssetName] = {
      if (assetName.length < MinAssetNameLength) {
        Some(new WrongAssetName(s"${assetName.length} < $MinAssetNameLength"))
      } else None
    }

    private[waves] def maxDescriptorNameLength(description: Array[Byte]): Option[WrongAssetDescription] = {
      if (description.length > MaxDescriptionLength) {
        Some(new WrongAssetDescription(s"${description.length} > $MaxDescriptionLength"))
      } else None
    }

    private[waves] def negativeDecimals(decimals: Int): Option[WrongAssetDecimals] = {
      if (decimals < 0) {
        Some(new WrongAssetDecimals(s"$decimals < 0"))
      } else None
    }

    private[waves] def maxDecimals(decimals: Int): Option[WrongAssetDecimals] = {
      if (decimals > MaxDecimals) {
        Some(new WrongAssetDecimals(s"$decimals > $MaxDecimals"))
      } else None
    }

    override def validate(tx: IssueTransaction)(implicit blockChain: WavesTransactionsValidators.this.type): Either[Seq[TransactionValidationError[IssueTransaction]], IssueTransaction] = {
      val errors = Seq(
        addressValidation(tx.recipient),
        smallFeeValidation(tx.fee),
        maxAssetNameLength(tx.name),
        minAssetNameLength(tx.name),
        maxDescriptorNameLength(tx.description),
        negativeDecimals(tx.decimals),
        maxDecimals(tx.decimals),
        negativeAmountValidation(tx.amount),
        overflowValidation(tx.issue, tx.feeMoney)
      ).flatten
      if (errors.nonEmpty) Left(errors) else Right(tx)
    }
  }

  object ReissueTransactionValidator extends AbstractTransactionValidator[ReissueTransaction] {
    override def validate(tx: ReissueTransaction)(implicit blockChain: WavesTransactionsValidators.this.type): Either[Seq[TransactionValidationError[ReissueTransaction]], ReissueTransaction] = {
      val errors = Seq(
        addressValidation(tx.recipient),
        negativeAmountValidation(tx.amount),
        negativeFeeValidation(tx.fee),
        overflowValidation(tx.issue, tx.feeMoney)
      ).flatten
      if (errors.nonEmpty) Left(errors) else Right(tx)
    }
  }

  class WrongAddress(message: => String) extends TransactionValidationError(message)

  class WrongAttachmentSize(message: => String) extends TransactionValidationError(message)

  class WrongAmount(message: => String) extends TransactionValidationError(message)

  class WrongFee(message: => String) extends TransactionValidationError(message)

  class Overflow(message: => String) extends TransactionValidationError(message)

  class WrongSignature(message: => String) extends TransactionValidationError(message)

  class WrongAssetName(message: => String) extends TransactionValidationError(message)

  class WrongAssetDescription(message: => String) extends TransactionValidationError(message)

  class WrongAssetDecimals(message: => String) extends TransactionValidationError(message)

  object TransferTransactionValidator extends AbstractTransactionValidator[TransferTransaction] {
    override def validate(tx: TransferTransaction)(implicit blockChain: WavesTransactionsValidators.this.type): Either[Seq[TransactionValidationError[TransferTransaction]], TransferTransaction] = {
      val errors = Seq(
        addressValidation(tx.recipient),
        attachmentSizeValidation(tx.attachment),
        negativeAmountValidation(tx.amount),
        negativeFeeValidation(tx.fee),
        overflowValidation(tx.transfer, tx.feeMoney)
      ).flatten
      if (errors.nonEmpty) Left(errors) else Right(tx)
    }
  }

  override protected def txValidator: SignedTransactionValidator[Transaction, SignedTransaction[Transaction]] = ???
//  new TransactionValidator[T] {
//    override def validate(tx: T)(implicit blockChain: WavesTransactionsValidators.this.type): Either[Seq[TransactionValidationError[Transaction]], Transaction] = {
//      tx match {
//          // todo they are not signed
//        case t: GenesisTransaction => GenesisTransactionValidator.validate(t)
//        case t: PaymentTransaction => PaymentTransactionValidator.validate(t)
//        case t: IssueTransaction => IssueTransactionValidator.validate(t)
//        case t: ReissueTransaction => ReissueTransactionValidator.validate(t)
//        case t: TransferTransaction => TransferTransactionValidator.validate(t)
//      }
//    }
//  }
}

trait WavesBlocksValidators {
  self: WavesBlockChain =>
  override protected def blockValidator: SignedBlockValidator[Block, Nothing] = ???
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
  final type ST[TX <: T] = SignedTransaction[TX]
  final type B = Block
  final type SB[BL <: B] = Nothing //BlockChainSignedBlock[BL, Array[Byte], ArraySignature64]
  final type AС = Account
  final type AD = Address

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

  def apply(b: WavesBlockChain#B): Unit

  // todo tree storage
  def jumpToState(b: WavesBlockChain#B): Unit
}
