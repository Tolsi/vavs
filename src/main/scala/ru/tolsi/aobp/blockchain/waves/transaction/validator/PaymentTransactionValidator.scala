package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction.PaymentTransaction

import scala.util.{Left, Right}

private[validator] class PaymentTransactionValidator extends AbstractTransactionValidator[PaymentTransaction] {
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
