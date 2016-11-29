package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.transaction.{GenesisTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}

import scala.util.{Left, Right}

private[validator] class GenesisTransactionValidator extends TransactionValidator[GenesisTransaction] {
//  override def validate(tx: GenesisTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesTransaction]], GenesisTransaction] = {
//    val errors = Seq(
//      addressValidation(tx.recipient),
//      negativeAmountValidation(tx.quantity)
//    ).flatten
//    if (errors.nonEmpty) Left(errors) else Right(tx)
//  }
  override def validate(tx: GenesisTransaction)(implicit bc: WavesBlockChain): ResultT = ???
}
