package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base.TransactionValidator
import ru.tolsi.aobp.blockchain.waves.transaction.validator.error._
import ru.tolsi.aobp.blockchain.waves.{Asset, Waves, WavesBlockChain, WavesMoney}

import scala.util.Try

private[validator] abstract class AbstractTransactionValidator[+TX <: WavesBlockChain#T] extends TransactionValidator[WavesBlockChain, TX] {
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
