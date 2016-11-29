package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.transaction.{TransferTransaction, WavesTransaction}

import scala.util.{Left, Right}

private[validator] class TransferTransactionValidator extends TransactionValidator[TransferTransaction] {
  override def validate(tx: TransferTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesTransaction]], TransferTransaction] = {
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
