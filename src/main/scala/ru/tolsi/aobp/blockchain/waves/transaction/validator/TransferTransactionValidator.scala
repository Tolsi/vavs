package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction.TransferTransaction

import scala.util.{Left, Right}

private[validator] class TransferTransactionValidator extends AbstractTransactionValidator[TransferTransaction] {
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
