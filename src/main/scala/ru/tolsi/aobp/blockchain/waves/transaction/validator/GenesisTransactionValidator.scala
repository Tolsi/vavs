package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.transaction.{GenesisTransaction, WavesTransaction}

import scala.util.{Left, Right}

private[validator] class GenesisTransactionValidator extends AbstractTransactionValidator[GenesisTransaction] {
  override def validate(tx: GenesisTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesTransaction]], GenesisTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      negativeAmountValidation(tx.quantity)
    ).flatten
    if (errors.nonEmpty) Left(errors) else Right(tx)
  }
}
