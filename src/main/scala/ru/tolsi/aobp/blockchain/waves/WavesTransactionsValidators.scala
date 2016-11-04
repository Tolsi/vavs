package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{Signature, Signer}

import scala.util.{Left, Right, Try}

trait WavesTransactionsValidators {
  self: WavesBlockChain =>

  case class WavesTransactionValidationParameters(blockTimestamp: Long) extends BlockTransactionParameters
  abstract class AbstractSignedTransactionWithTimeValidator[STX <: ST[T]](blockTimestamp: Long) extends AbstractSignedTransactionValidator[T, STX]

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

  implicit object AnySignedTransactionValidator extends AbstractSignedTransactionValidator[T, ST[T]] {
    val signer = implicitly[Signer[WavesBlockChain, T, Array[Byte], ArraySignature64]]
    private[waves] def signatureValidation(tx: ST[T], signature: Signature[Array[Byte]]): Option[WrongSignature] = {
      // todo rewrite
      if (signer.sign(tx.signed).signature.value sameElements tx.signature.value) {
        Some(new WrongSignature(s"Signature is not valid"))
      } else None
    }

    override def validate(stx: ST[T]): Either[Seq[TransactionValidationError[ST[T]]], ST[T]] = {
      UnsignedTransactionValidator.validate(stx.signed) match {
        case Left(errors) =>
          // todo it works? see definitions todo if not
          Left(errors.map(_.asInstanceOf[TransactionValidationError[ST[T]]]))
        case Right(_) =>
          val signatureError = signatureValidation(stx, stx.signature)
          if (signatureError.isDefined) {
            Left(Seq(signatureError.get))
          } else {
            Right(stx)
          }
      }
    }
  }

  class AnySignedTransactionWithTimeValidator(blockTimestamp: Long)
                                                           (implicit signer: Signer[WavesBlockChain, T, Array[Byte], ArraySignature64],
                                                                          txValidator: TransactionValidator[T],
                                                                          signedTxValidator: AbstractSignedTransactionValidator[T, ST[T]])
    extends AbstractSignedTransactionWithTimeValidator[ST[T]](blockTimestamp) {

    private[waves] def timestampValidation(tx: ST[T], blockTimestamp: Long): Option[WrongTimestamp] = {
      if (tx.timestamp - blockTimestamp < configuration.maxTimeDriftMillis) {
        Some(new WrongTimestamp(s"Signature is not valid"))
      } else None
    }

    override def validate(stx: ST[T]): Either[Seq[TransactionValidationError[ST[T]]], ST[T]] = {
      signedTxValidator.validate(stx) match {
        case Left(errors) =>
          Left(errors)
        case Right(_) =>
          val txTimestamp = timestampValidation(stx, blockTimestamp)
          if (txTimestamp.isDefined) {
            Left(Seq(txTimestamp.get))
          } else {
            Right(stx)
          }
      }
    }
  }

  implicit object GenesisTransactionValidator extends AbstractTransactionValidator[GenesisTransaction] {
    override def validate(tx: GenesisTransaction): Either[Seq[TransactionValidationError[GenesisTransaction]], GenesisTransaction] = {
      val errors = Seq(
        addressValidation(tx.recipient),
        negativeAmountValidation(tx.amount)
      ).flatten
      if (errors.nonEmpty) Left(errors) else Right(tx)
    }
  }

  implicit object PaymentTransactionValidator extends AbstractTransactionValidator[PaymentTransaction] {
    override def validate(tx: PaymentTransaction): Either[Seq[TransactionValidationError[PaymentTransaction]], PaymentTransaction] = {
      val errors = Seq(
        addressValidation(tx.recipient),
        negativeAmountValidation(tx.amount),
        negativeFeeValidation(tx.fee),
        overflowValidation(tx.amount, tx.fee)
      ).flatten
      if (errors.nonEmpty) Left(errors) else Right(tx)
    }
  }

  implicit object IssueTransactionValidator extends AbstractTransactionValidator[IssueTransaction] {
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

    override def validate(tx: IssueTransaction): Either[Seq[TransactionValidationError[IssueTransaction]], IssueTransaction] = {
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

  implicit object ReissueTransactionValidator extends AbstractTransactionValidator[ReissueTransaction] {
    override def validate(tx: ReissueTransaction): Either[Seq[TransactionValidationError[ReissueTransaction]], ReissueTransaction] = {
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

  class WrongTimestamp(message: => String) extends TransactionValidationError(message)

  class WrongAssetName(message: => String) extends TransactionValidationError(message)

  class WrongAssetDescription(message: => String) extends TransactionValidationError(message)

  class WrongAssetDecimals(message: => String) extends TransactionValidationError(message)

  implicit object TransferTransactionValidator extends AbstractTransactionValidator[TransferTransaction] {
    override def validate(tx: TransferTransaction): Either[Seq[TransactionValidationError[TransferTransaction]], TransferTransaction] = {
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

  implicit object UnsignedTransactionValidator extends TransactionValidator[T] {
    private def implicitlyValidate[TX <: T](tx: TX)(implicit validator:AbstractTransactionValidator[TX]): Either[Seq[TransactionValidationError[TX]], TX] = {
      validator.validate(tx)
    }
    override def validate(tx: T):
    Either[Seq[TransactionValidationError[Transaction]], Transaction] = {
      tx match {
        case tx: GenesisTransaction => implicitlyValidate(tx)
        case tx: PaymentTransaction => implicitlyValidate(tx)
        case tx: IssueTransaction => implicitlyValidate(tx)
        case tx: ReissueTransaction => implicitlyValidate(tx)
        case tx: TransferTransaction => implicitlyValidate(tx)
      }
    }
  }

  // todo make object and implicitly?
  class SignedTransactionValidator(implicit signer: Signer[WavesBlockChain, T, Array[Byte], ArraySignature64],
                                   unsignedTxValidator: TransactionValidator[T],
                                   signedValidator: AbstractSignedTransactionValidator[T, ST[T]]
                                  ) extends AbstractSignedTransactionValidator[T, ST[T]] {
    override def validate(stx: SignedTransaction[Transaction]): Either[Seq[TransactionValidationError[SignedTransaction[Transaction]]], SignedTransaction[Transaction]] = {
      unsignedTxValidator.validate(stx) match {
        case Left(errors)=>
          // todo it works? see definitions todo if not
          Left(errors.map(_.asInstanceOf[TransactionValidationError[ST[T]]]))
        case Right(errors)=>
          signedValidator.validate(stx)
      }
    }
  }

  override protected def txValidator(bvp: TVP): AnySignedTransactionWithTimeValidator = {
    val signer = implicitly[Signer[WavesBlockChain, T, Array[Byte], ArraySignature64]]
    new AnySignedTransactionWithTimeValidator(bvp.blockTimestamp)(signer, UnsignedTransactionValidator,
      new SignedTransactionValidator()(signer, UnsignedTransactionValidator, new AnySignedTransactionWithTimeValidator(bvp.blockTimestamp)))
  }

}
