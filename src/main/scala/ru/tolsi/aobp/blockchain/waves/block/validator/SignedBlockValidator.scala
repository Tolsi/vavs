package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.waves._
import ru.tolsi.aobp.blockchain.waves.block.validator.error.WrongSignature
import ru.tolsi.aobp.blockchain.waves.block.{WavesSignedBlock, WavesBlock}

class SignedBlockValidator(blockValidator: AbstractBlockValidator[WavesBlock],
                           signer: WavesSigner[WavesBlock, WavesSignedBlock[WavesBlock], Signature64]) extends AbstractSignedBlockValidator[WavesBlock, WavesSignedBlock[WavesBlock]] {
  private def validateSignature(b: WavesSignedBlock[WavesBlock])(implicit wbc: WavesBlockChain): Option[WrongSignature] = {
    if (signer.sign(b.signed).signature != b.signature) {
      Some(new WrongSignature("Signature is not valid"))
    } else None
  }

  override def validate(b: WavesSignedBlock[WavesBlock])(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[WavesBlock]], WavesBlock] = {
    blockValidator.validate(b.signed) match {
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
