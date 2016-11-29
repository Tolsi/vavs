package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves._
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction
import ru.tolsi.aobp.blockchain.waves.transaction.validator.error.WrongTimestamp

import scala.util.{Left, Right}

private[waves] class SignedTransactionWithTimeValidators(timestamp: Long, signer: Signer[WavesTransaction, SignedTransaction[WavesTransaction], Signature64])(implicit txValidator: TransactionValidator[WavesTransaction], signedTxValidator: AbstractSignedTransactionValidator[WavesTransaction, SignedTransaction[WavesTransaction]])
  extends AbstractSignedTransactionWithTimeValidator[SignedTransaction[WavesTransaction]](timestamp) {

  private[waves] def timestampValidation(tx: SignedTransaction[WavesTransaction], blockTimestamp: Long)(implicit wbc: WavesBlockChain): Option[WrongTimestamp] = {
    if (tx.timestamp - blockTimestamp < wbc.configuration.maxTimeDriftMillis) {
      Some(new WrongTimestamp(s"Transaction is far away in future: ${tx.timestamp} - $blockTimestamp < ${wbc.configuration.maxTimeDriftMillis}"))
    } else if (blockTimestamp - tx.timestamp < wbc.configuration.maxTxAndBlockDiffMillis) {
      Some(new WrongTimestamp(s"Transaction is too old: $blockTimestamp - ${tx.timestamp} < ${wbc.configuration.maxTxAndBlockDiffMillis}"))
    } else None
  }

  override def validate(stx: SignedTransaction[WavesTransaction])(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesTransaction]], WavesTransaction] = {
    signedTxValidator.validate(stx) match {
      case Left(errors) =>
        Left(errors)
      case Right(_) =>
        val txTimestamp = timestampValidation(stx, timestamp)
        if (txTimestamp.isDefined) {
          Left(Seq(txTimestamp.get))
        } else {
          Right(stx)
        }
    }
  }
}
