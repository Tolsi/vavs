package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.transaction.{PaymentTransaction, WavesTransaction}

import scala.util.{Left, Right}

private[validator] class PaymentTransactionValidator extends AbstractTransactionValidator[PaymentTransaction] {
  override def validate(tx: PaymentTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, WavesTransaction]], PaymentTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      negativeAmountValidation(tx.quantity),
      negativeFeeValidation(tx.fee),
      overflowValidation(tx.quantity, tx.fee)
    ).flatten
    if (errors.nonEmpty) Left(errors) else Right(tx)
  }
}
