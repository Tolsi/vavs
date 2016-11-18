package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.base.AbstractBlockValidator
import ru.tolsi.aobp.blockchain.waves.{AbstractBlockValidator, BlockValidationError, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.block.BaseBlock

class BaseBlockValidator extends AbstractBlockValidator[WavesBlockChain, BaseBlock] {
  override def validate(tx: BaseBlock)(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[WavesBlockChain, BaseBlock]], WavesBlockChain#B] = ???
}
