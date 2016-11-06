package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction.ReissueTransaction

import scala.util.{Left, Right}

private[validator] class ReissueTransactionValidator extends AbstractTransactionValidator[ReissueTransaction] {
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
