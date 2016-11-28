package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.transaction.validator.error._
import ru.tolsi.aobp.blockchain.waves._
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction

import scala.util.Try

private[validator] abstract class AbstractTransactionValidator[TX <: WavesTransaction] extends TransactionValidator[TX] {
  private[waves] val MaxAttachmentSize = 140

  private[waves] def addressValidation(address: Address)(implicit wbc: WavesBlockChain): Option[WrongAddress] = {
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
