package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves._
import ru.tolsi.aobp.blockchain.waves.transaction.{SignedTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves.transaction.validator.error.WrongSignature

import scala.util.{Left, Right}

private[validator] class SignedTransactionValidator(implicit signer: Signer[WavesTransaction, SignedTransaction[WavesTransaction], Signature64],
                                                    unsignedTxValidator: TransactionValidator[WavesTransaction])
  extends AbstractSignedTransactionValidator[WavesTransaction, WavesBlockChain#ST[WavesTransaction]] {
  private[waves] def signatureValidation(tx: WavesBlockChain#ST[WavesTransaction])(implicit wbc: WavesBlockChain): Option[WrongSignature] = {
    if (signer.sign(tx.signed).signature != tx.signature) {
      Some(new WrongSignature(s"Signature is not valid"))
    } else None
  }

  override def validate(stx: WavesBlockChain#ST[WavesTransaction])(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesTransaction]], WavesTransaction] = {
    unsignedTxValidator.validate(stx.signed) match {
      case Left(errors) =>
        Left(errors)
      case Right(_) =>
        val signatureError = signatureValidation(stx)
        if (signatureError.isDefined) {
          Left(Seq(signatureError.get))
        } else {
          Right(stx)
        }
    }
  }
}
