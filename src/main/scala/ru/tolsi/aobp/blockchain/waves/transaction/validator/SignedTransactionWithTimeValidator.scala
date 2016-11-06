package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction.validator.error.WrongTimestamp

import scala.util.{Left, Right}

private[waves] class SignedTransactionWithTimeValidator(timestamp: Long)
                                                       (implicit signer: Signer[WavesBlockChain, WavesBlockChain#T, Signature64],
                                                        txValidator: TransactionValidator[WavesBlockChain, WavesBlockChain#T],
                                                        signedTxValidator: AbstractSignedTransactionValidator[WavesBlockChain, WavesBlockChain#T, WavesBlockChain#ST[WavesBlockChain#T]])
  extends AbstractSignedTransactionWithTimeValidator[WavesBlockChain#ST[WavesBlockChain#T]](timestamp) {

  private[waves] def timestampValidation(tx: WavesBlockChain#ST[WavesBlockChain#T], blockTimestamp: Long)(implicit wbc: WavesBlockChain): Option[WrongTimestamp] = {
    if (tx.timestamp - blockTimestamp < wbc.configuration.maxTimeDriftMillis) {
      Some(new WrongTimestamp(s"Transaction is far away in future: ${tx.timestamp} - $blockTimestamp < ${wbc.configuration.maxTimeDriftMillis}"))
    } else if (blockTimestamp - tx.timestamp < wbc.configuration.maxTxAndBlockDiffMillis) {
      Some(new WrongTimestamp(s"Transaction is too old: $blockTimestamp - ${tx.timestamp} < ${wbc.configuration.maxTxAndBlockDiffMillis}"))
    } else None
  }

  override def validate(stx: WavesBlockChain#ST[WavesBlockChain#T])(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, WavesBlockChain#T]], WavesBlockChain#T] = {
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
