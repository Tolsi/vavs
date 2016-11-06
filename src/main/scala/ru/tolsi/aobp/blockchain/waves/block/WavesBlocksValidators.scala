package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesSigner}

object GenesisBlockValidator extends AbstractBlockValidator[WavesBlockChain, GenesisBlock] {
  override def validate(tx: GenesisBlock)(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[WavesBlockChain, GenesisBlock]], WavesBlockChain#B] = ???
}

object BaseBlockValidator extends AbstractBlockValidator[WavesBlockChain, BaseBlock] {
  override def validate(tx: BaseBlock)(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[WavesBlockChain, BaseBlock]], WavesBlockChain#B] = ???
}

class SignatureError(message: => String) extends SignedBlockValidationError(message)

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

object UnsignedBlockValidator extends AbstractBlockValidator[WavesBlockChain, WavesBlock] {
  private def implicitlyValidate[BL <: WavesBlockChain#B](b: BL)(implicit validator: AbstractBlockValidator[WavesBlockChain, BL]): Either[Seq[BlockValidationError[WavesBlockChain, BL]], BL] = {
    validator.validate(b)
  }

  override def validate(b: WavesBlock)(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[WavesBlockChain, WavesBlock]], WavesBlock] = {
    b match {
      // todo validate transactions
      // todo txs in correct order
      // todo not from future and from last block time, see original waves
      case b: GenesisBlock => implicitlyValidate(b)
      case b: BaseBlock => implicitlyValidate(b)
    }
  }
}

