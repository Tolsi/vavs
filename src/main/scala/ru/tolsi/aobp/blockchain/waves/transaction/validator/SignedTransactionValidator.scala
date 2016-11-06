package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction.SignedTransaction
import ru.tolsi.aobp.blockchain.waves.transaction.validator.error.WrongSignature

import scala.util.{Left, Right}

private[validator] class SignedTransactionValidator(implicit signer: Signer[WavesBlockChain, WavesBlockChain#T, SignedTransaction[WavesBlockChain#T], Signature64],
                                                    unsignedTxValidator: TransactionValidator[WavesBlockChain, WavesBlockChain#T])
  extends AbstractSignedTransactionValidator[WavesBlockChain, WavesBlockChain#T, WavesBlockChain#ST[WavesBlockChain#T]] {
  private[waves] def signatureValidation(tx: WavesBlockChain#ST[WavesBlockChain#T])(implicit wbc: WavesBlockChain): Option[WrongSignature] = {
    if (signer.sign(tx.signed).signature != tx.signature) {
      Some(new WrongSignature(s"Signature is not valid"))
    } else None
  }

  override def validate(stx: WavesBlockChain#ST[WavesBlockChain#T])(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, WavesBlockChain#T]], WavesBlockChain#T] = {
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
