package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.base.{AbstractBlockValidator, AbstractSignedBlockValidator, Signature64, SignedBlockValidationError}
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.block.{SignedBlock, WavesBlock}
import ru.tolsi.aobp.blockchain.waves.transaction.signer.WavesSigner

class SignedBlockValidator(implicit blockValidator: AbstractBlockValidator[WavesBlockChain, WavesBlock]) extends AbstractSignedBlockValidator[WavesBlockChain, WavesBlock, WavesBlockChain#SB[WavesBlock]] {
  private def validateSignature(b: SignedBlock[WavesBlock])(implicit signer: WavesSigner[WavesBlockChain#B, Signature64]): Option[SignatureError] = {
    if (signer.sign(b).signature != b.signature) {
      Some(new SignatureError("Signature is not valid"))
    } else None
  }

  override def validate(b: SignedBlock[WavesBlock])(implicit wbc: WavesBlockChain): Either[Seq[SignedBlockValidationError[WavesBlockChain, WavesBlockChain#B]], WavesBlockChain#B] = {
    blockValidator.validate(b) match {
      case Left(errors) => Left(errors)
      case Right(tx) =>
        val signatureError = validateSignature(b)
        if (signatureError.isDefined) {
          Left(Seq(signatureError.get))
        } else {
          Right(b)
        }
    }
  }
}
