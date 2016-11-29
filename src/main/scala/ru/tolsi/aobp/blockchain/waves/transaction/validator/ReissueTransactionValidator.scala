package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.transaction.{ReissueTransaction, WavesTransaction}

import scala.util.{Left, Right}

private[validator] class ReissueTransactionValidator extends TransactionValidator[ReissueTransaction] {
  override def validate(tx: ReissueTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesTransaction]], ReissueTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      negativeAmountValidation(tx.quantity),
      negativeFeeValidation(tx.fee),
      overflowValidation(tx.issue, tx.feeMoney)
    ).flatten
    if (errors.nonEmpty) Left(errors) else Right(tx)
  }
}
