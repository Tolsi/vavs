package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{ArraySignCreator, Signature64}

trait WavesBlocksValidators {
  self: WavesBlockChain =>

  implicit object GenesisBlockValidator extends AbstractBlockValidator[GenesisBlock] {
    override def validate(tx: GenesisBlock): Either[Seq[BlockValidationError[GenesisBlock]], GenesisBlock] = ???
  }
  implicit object BaseBlockValidator extends AbstractBlockValidator[BaseBlock] {
    override def validate(tx: BaseBlock): Either[Seq[BlockValidationError[BaseBlock]], BaseBlock] = ???
  }

  class SignatureError(message: => String) extends SignedBlockValidationError(message)
  class SignedBlockValidator(implicit blockValidator: AbstractBlockValidator[WavesBlock]) extends AbstractSignedBlockValidator[WavesBlock, SB[WavesBlock]] {
    private def validateSignature(b: SignedBlock[WavesBlock])(implicit signer: WavesSigner[B, Signature64]): Option[SignatureError] = {
      if (signer.sign(b).signature != b.signature) {
        Some(new SignatureError("Signature is not valid"))
      } else None
    }

    override def validate(b: SignedBlock[WavesBlock]): Either[Seq[SignedBlockValidationError[SB[B]]], SB[B]] = {
      blockValidator.validate(b) match {
        case Left(errors) => Left(errors.map(_.asInstanceOf[SignedBlockValidationError[SB[B]]]))
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

  implicit object UnsignedBlockValidator extends AbstractBlockValidator[WavesBlock] {
    private def implicitlyValidate[BL <: B](b: BL)(implicit validator:AbstractBlockValidator[BL]): Either[Seq[BlockValidationError[BL]], BL] = {
      validator.validate(b)
    }

    override def validate(b: WavesBlock): Either[Seq[BlockValidationError[WavesBlock]], WavesBlock] = {
      b match {
          // todo validate transactions
        case b: GenesisBlock => implicitlyValidate(b)
        case b: BaseBlock => implicitlyValidate(b)
      }
    }
  }
  override protected val blockValidator: AbstractSignedBlockValidator[WavesBlock, SB[WavesBlock]] = new SignedBlockValidator
}
