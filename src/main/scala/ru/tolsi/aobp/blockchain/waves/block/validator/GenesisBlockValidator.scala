package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.base.{AbstractBlockValidator, BlockValidationError}
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.block.GenesisBlock

class GenesisBlockValidator extends AbstractBlockValidator[WavesBlockChain, GenesisBlock] {
  override def validate(tx: GenesisBlock)(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[WavesBlockChain, GenesisBlock]], WavesBlockChain#B] = ???
}
