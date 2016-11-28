package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.waves.{AbstractBlockValidator, BlockValidationError, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.block.{BaseBlock, WavesBlock}

class BaseBlockValidator extends AbstractBlockValidator[BaseBlock] {
  override def validate(tx: BaseBlock)(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[BaseBlock]], WavesBlock] = ???
}
