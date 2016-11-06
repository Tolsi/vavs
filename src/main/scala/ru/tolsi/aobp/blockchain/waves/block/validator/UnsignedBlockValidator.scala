package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.block.{BaseBlock, GenesisBlock, WavesBlock}


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

