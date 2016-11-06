package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.{Asset, Waves, WavesBlockChain, WavesMoney}

import scala.util.{Left, Right, Try}


case class WavesTransactionValidationParameters(blockTimestamp: Long) extends BlockTransactionParameters[WavesBlockChain]

abstract class AbstractSignedTransactionWithTimeValidator[STX <: WavesBlockChain#ST[WavesBlockChain#T]](blockTimestamp: Long) extends AbstractSignedTransactionValidator[WavesBlockChain, WavesBlockChain#T, STX]

abstract class AbstractTransactionValidator[+TX <: WavesBlockChain#T] extends TransactionValidator[WavesBlockChain, TX] {
  private[waves] val MaxAttachmentSize = 140

  private[waves] def addressValidation(address: WavesBlockChain#AD): Option[WrongAddress[TX]] = {
    address.validate.map(error => new WrongAddress(error.message))
  }

  private[waves] def attachmentSizeValidation(attachment: Array[Byte]): Option[WrongAttachmentSize[TX]] = {
    if (attachment.length > MaxAttachmentSize) {
      Some(new WrongAttachmentSize(s"${attachment.length} > $MaxAttachmentSize"))
    } else None
  }

  private[waves] def negativeAmountValidation(amount: Long): Option[WrongAmount[TX]] = {
    if (amount <= 0) {
      Some(new WrongAmount(s"$amount <= 0"))
    } else None
  }

  private[waves] def negativeFeeValidation(fee: Long): Option[WrongFee[TX]] = {
    if (fee <= 0) {
      Some(new WrongFee(s"$fee <= 0"))
    } else None
  }

  private[waves] def overflowValidation(amount: WavesMoney[_ <: Either[Waves.type, Asset]],
                                        fee: WavesMoney[_ <: Either[Waves.type, Asset]]): Option[Overflow[TX]] = {
    if (amount.currency == fee.currency &&
      Try(Math.addExact(amount.value, fee.value)).isFailure) {
      Some(new Overflow(s"${amount.currency}: $amount + $fee = ${amount.value + fee.value}"))
    } else None
  }

  private[waves] def overflowValidation(amount: Long,
                                        fee: Long): Option[Overflow[TX]] = {
    if (Try(Math.addExact(amount, fee)).isFailure) {
      Some(new Overflow(s"$amount + $fee = ${amount + fee}"))
    } else None
  }
}

object SignedTransactionValidator extends AbstractSignedTransactionValidator[WavesBlockChain, WavesBlockChain#T, WavesBlockChain#ST[WavesBlockChain#T]] {
  val signer = implicitly[Signer[WavesBlockChain, WavesBlockChain#T, Signature64]]

  private[waves] def signatureValidation(tx: WavesBlockChain#ST[WavesBlockChain#T]): Option[WrongSignature[WavesBlockChain#ST[WavesBlockChain#T]]] = {
    if (signer.sign(tx.signed).signature != tx.signature) {
      Some(new WrongSignature(s"Signature is not valid"))
    } else None
  }

  override def validate(stx: WavesBlockChain#ST[WavesBlockChain#T])(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, WavesBlockChain#T]], WavesBlockChain#T] = {
    UnsignedTransactionValidator.validate(stx.signed) match {
      case Left(errors) =>
        // todo it works? see definitions todo if not
        Left(errors)
      case Right(_) =>
        val signatureError = signatureValidation(stx)
        if (signatureError.isDefined) {
          Left(Seq(signatureError.get))
        } else {
          Right(stx)
        }
    }
  }
}

class SignedTransactionWithTimeValidator(blockTimestamp: Long)
                                        (implicit signer: Signer[WavesBlockChain, WavesBlockChain#T, Signature64],
                                         txValidator: TransactionValidator[WavesBlockChain, WavesBlockChain#T],
                                         signedTxValidator: AbstractSignedTransactionValidator[WavesBlockChain, WavesBlockChain#T, WavesBlockChain#ST[WavesBlockChain#T]])
  extends AbstractSignedTransactionWithTimeValidator[WavesBlockChain#ST[WavesBlockChain#T]](blockTimestamp) {

  private[waves] def timestampValidation(tx: WavesBlockChain#ST[WavesBlockChain#T], blockTimestamp: Long)(implicit wbc: WavesBlockChain): Option[WrongTimestamp[WavesBlockChain#ST[WavesBlockChain#T]]] = {
    if (tx.timestamp - blockTimestamp < wbc.configuration.maxTimeDriftMillis) {
      Some(new WrongTimestamp(s"Transaction is far away in future: ${tx.timestamp} - $blockTimestamp < ${wbc.configuration.maxTimeDriftMillis}"))
    } else if (blockTimestamp - tx.timestamp < wbc.configuration.maxTxAndBlockDiffMillis) {
      Some(new WrongTimestamp(s"Transaction is too old: $blockTimestamp - ${tx.timestamp} < ${wbc.configuration.maxTxAndBlockDiffMillis}"))
    } else None
  }

  override def validate(stx: WavesBlockChain#ST[WavesBlockChain#T])(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, WavesBlockChain#T]], WavesBlockChain#T] = {
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

object GenesisTransactionValidator extends AbstractTransactionValidator[GenesisTransaction] {
  override def validate(tx: GenesisTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, GenesisTransaction]], GenesisTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      negativeAmountValidation(tx.quantity)
    ).flatten
    if (errors.nonEmpty) Left(errors) else Right(tx)
  }
}

object PaymentTransactionValidator extends AbstractTransactionValidator[PaymentTransaction] {
  override def validate(tx: PaymentTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, PaymentTransaction]], PaymentTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      negativeAmountValidation(tx.quantity),
      negativeFeeValidation(tx.fee),
      overflowValidation(tx.quantity, tx.fee)
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

  private[waves] def smallFeeValidation(fee: Long): Option[WrongFee[IssueTransaction]] = {
    if (fee < MinFee) {
      Some(new WrongFee(s"$fee < $MinFee"))
    } else None
  }

  private[waves] def maxAssetNameLength(assetName: Array[Byte]): Option[WrongAssetName[IssueTransaction]] = {
    if (assetName.length > MaxAssetNameLength) {
      Some(new WrongAssetName(s"${assetName.length} > $MaxAssetNameLength"))
    } else None
  }

  private[waves] def minAssetNameLength(assetName: Array[Byte]): Option[WrongAssetName[IssueTransaction]] = {
    if (assetName.length < MinAssetNameLength) {
      Some(new WrongAssetName(s"${assetName.length} < $MinAssetNameLength"))
    } else None
  }

  private[waves] def maxDescriptorNameLength(description: Array[Byte]): Option[WrongAssetDescription[IssueTransaction]] = {
    if (description.length > MaxDescriptionLength) {
      Some(new WrongAssetDescription(s"${description.length} > $MaxDescriptionLength"))
    } else None
  }

  private[waves] def negativeDecimals(decimals: Int): Option[WrongAssetDecimals[IssueTransaction]] = {
    if (decimals < 0) {
      Some(new WrongAssetDecimals(s"$decimals < 0"))
    } else None
  }

  private[waves] def maxDecimals(decimals: Int): Option[WrongAssetDecimals[IssueTransaction]] = {
    if (decimals > MaxDecimals) {
      Some(new WrongAssetDecimals(s"$decimals > $MaxDecimals"))
    } else None
  }

  override def validate(tx: IssueTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, IssueTransaction]], IssueTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      smallFeeValidation(tx.fee),
      maxAssetNameLength(tx.name),
      minAssetNameLength(tx.name),
      maxDescriptorNameLength(tx.description),
      negativeDecimals(tx.decimals),
      maxDecimals(tx.decimals),
      negativeAmountValidation(tx.quantity),
      overflowValidation(tx.issue, tx.feeMoney)
    ).flatten
    if (errors.nonEmpty) Left(errors) else Right(tx)
  }
}

object ReissueTransactionValidator extends AbstractTransactionValidator[ReissueTransaction] {
  override def validate(tx: ReissueTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, ReissueTransaction]], ReissueTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      negativeAmountValidation(tx.quantity),
      negativeFeeValidation(tx.fee),
      overflowValidation(tx.issue, tx.feeMoney)
    ).flatten
    if (errors.nonEmpty) Left(errors) else Right(tx)
  }
}

class WrongAddress[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

class WrongAttachmentSize[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

class WrongAmount[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

class WrongFee[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

class Overflow[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

class WrongSignature[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

class WrongTimestamp[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

class WrongAssetName[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

class WrongAssetDescription[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

class WrongAssetDecimals[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)

object TransferTransactionValidator extends AbstractTransactionValidator[TransferTransaction] {
  override def validate(tx: TransferTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, TransferTransaction]], TransferTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      attachmentSizeValidation(tx.attachment),
      negativeAmountValidation(tx.quantity),
      negativeFeeValidation(tx.fee),
      overflowValidation(tx.transfer, tx.feeMoney)
    ).flatten
    if (errors.nonEmpty) Left(errors) else Right(tx)
  }
}

object UnsignedTransactionValidator extends TransactionValidator[WavesBlockChain, WavesBlockChain#T] {
  private def implicitlyValidate[TX <: WavesBlockChain#T](tx: TX)(implicit validator: AbstractTransactionValidator[TX]): Either[Seq[TransactionValidationError[WavesBlockChain, TX]], WavesBlockChain#T] = {
    validator.validate(tx)
  }

  override def validate(tx: WavesBlockChain#T)(implicit wbc: WavesBlockChain):
  Either[Seq[TransactionValidationError[WavesBlockChain, WavesBlockChain#T]], WavesTransaction] = {
    tx match {
      // todo all state changes validation
      case tx: GenesisTransaction => implicitlyValidate(tx)
      case tx: PaymentTransaction => implicitlyValidate(tx)
      case tx: IssueTransaction => implicitlyValidate(tx)
      case tx: ReissueTransaction => implicitlyValidate(tx)
      case tx: TransferTransaction => implicitlyValidate(tx)
    }
  }
}

// todo make object and implicitly?
class SignedTransactionValidator(implicit signer: Signer[WavesBlockChain, WavesBlockChain#T, Signature64],
                                 unsignedTxValidator: TransactionValidator[WavesBlockChain, WavesBlockChain#T],
                                 signedValidator: AbstractSignedTransactionValidator[WavesBlockChain, WavesBlockChain#T, WavesBlockChain#ST[WavesBlockChain#T]]
                                ) extends AbstractSignedTransactionValidator[WavesBlockChain, WavesBlockChain#T, WavesBlockChain#ST[WavesBlockChain#T]] {
  override def validate(stx: SignedTransaction[WavesTransaction])(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, WavesTransaction]], WavesTransaction] = {
    unsignedTxValidator.validate(stx) match {
      case Left(errors) =>
        // todo it works? see definitions todo if not
        Left(errors)
      case Right(errors) =>
        signedValidator.validate(stx)
    }
  }
}
