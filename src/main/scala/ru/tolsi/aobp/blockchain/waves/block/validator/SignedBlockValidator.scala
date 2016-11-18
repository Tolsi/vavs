package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.base.{AbstractBlockValidator, AbstractSignedBlockValidator, BlockValidationError}
import ru.tolsi.aobp.blockchain.waves._
import ru.tolsi.aobp.blockchain.waves.block.validator.error.WrongSignature
import ru.tolsi.aobp.blockchain.waves.block.{SignedBlock, WavesBlock}

class SignedBlockValidator(implicit blockValidator: AbstractBlockValidator[WavesBlockChain, WavesBlock],
                           signer: WavesSigner[WavesBlockChain#B, SignedBlock[WavesBlockChain#B], Signature64]) extends AbstractSignedBlockValidator[WavesBlockChain, WavesBlockChain#B, WavesBlockChain#SB[WavesBlockChain#B]] {
  private def validateSignature(b: WavesBlockChain#SB[WavesBlockChain#B])(implicit wbc: WavesBlockChain): Option[WrongSignature] = {
    if (signer.sign(b).signature != b.signature) {
      Some(new WrongSignature("Signature is not valid"))
    } else None
  }

  override def validate(b: SignedBlock[WavesBlock])(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[WavesBlockChain, WavesBlockChain#B]], WavesBlockChain#B] = {
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
