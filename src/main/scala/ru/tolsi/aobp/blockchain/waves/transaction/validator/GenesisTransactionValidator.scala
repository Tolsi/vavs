package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction.GenesisTransaction

import scala.util.{Left, Right}

private[validator] class GenesisTransactionValidator extends AbstractTransactionValidator[GenesisTransaction] {
  override def validate(tx: GenesisTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, GenesisTransaction]], GenesisTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      negativeAmountValidation(tx.quantity)
    ).flatten
    if (errors.nonEmpty) Left(errors) else Right(tx)
  }
}
