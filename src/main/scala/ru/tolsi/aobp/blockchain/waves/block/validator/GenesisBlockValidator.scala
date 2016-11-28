package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.waves.{AbstractBlockValidator, BlockValidationError, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.block.{GenesisBlock, WavesBlock}

class GenesisBlockValidator extends AbstractBlockValidator[GenesisBlock] {
  override def validate(tx: GenesisBlock)(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[GenesisBlock]], WavesBlock] = ???
}
