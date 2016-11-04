package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{Signature, Signer}

import scala.util.Try

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

  abstract class AbstractSignedTransactionValidator[TX <: T, STX <: ST[TX]](implicit signer: Signer[WavesBlockChain, TX, Array[Byte], ArraySignature64],
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
            Left(Seq(signatureError.get))
          } else {
            Right(stx)
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
