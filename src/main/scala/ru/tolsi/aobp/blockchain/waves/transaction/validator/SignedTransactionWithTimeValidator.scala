package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves._
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction
import ru.tolsi.aobp.blockchain.waves.transaction.validator.error.WrongTimestamp

import scala.util.{Left, Right}

private[waves] class SignedTransactionWithTimeValidator(timestamp: Long)
                                                       (implicit signer: Signer[WavesTransaction, ST[WavesTransaction], Signature64],
                                                        txValidator: TransactionValidator[WavesTransaction],
                                                        signedTxValidator: AbstractSignedTransactionValidator[WavesTransaction, ST[WavesTransaction]])
  extends AbstractSignedTransactionWithTimeValidator[ST[WavesTransaction]](timestamp) {

  private[waves] def timestampValidation(tx: ST[WavesTransaction], blockTimestamp: Long)(implicit wbc: WavesBlockChain): Option[WrongTimestamp] = {
    if (tx.timestamp - blockTimestamp < wbc.configuration.maxTimeDriftMillis) {
      Some(new WrongTimestamp(s"Transaction is far away in future: ${tx.timestamp} - $blockTimestamp < ${wbc.configuration.maxTimeDriftMillis}"))
    } else if (blockTimestamp - tx.timestamp < wbc.configuration.maxTxAndBlockDiffMillis) {
      Some(new WrongTimestamp(s"Transaction is too old: $blockTimestamp - ${tx.timestamp} < ${wbc.configuration.maxTxAndBlockDiffMillis}"))
    } else None
  }

  override def validate(stx: ST[WavesTransaction])(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesTransaction]], WavesTransaction] = {
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
