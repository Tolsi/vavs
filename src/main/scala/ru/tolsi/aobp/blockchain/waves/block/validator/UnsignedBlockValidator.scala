package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.block.{BaseBlock, GenesisBlock, WavesBlock}

import ru.tolsi.aobp.blockchain.waves.block.validator._

class UnsignedBlockValidator extends AbstractBlockValidator[WavesBlockChain, WavesBlockChain#B] {
  private def implicitlyValidate[BL <: WavesBlockChain#B](b: BL)(implicit wbc: WavesBlockChain, validator: AbstractBlockValidator[WavesBlockChain, BL]): Either[Seq[BlockValidationError[WavesBlockChain, WavesBlockChain#B]], WavesBlockChain#B] = {
    validator.validate(b)
  }

  override def validate(b: WavesBlockChain#B)(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[WavesBlockChain, WavesBlockChain#B]], WavesBlockChain#B] = {
    b match {
      // todo validate transactions
      // todo txs in correct order
      // todo not from future and from last block time, see original waves
      case b: GenesisBlock => implicitlyValidate(b)
      case b: BaseBlock => implicitlyValidate(b)
    }
  }
}

