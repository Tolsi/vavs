package ru.tolsi.aobp.blockchain.waves

trait WavesBlocksValidators {
  self: WavesBlockChain =>
  override protected def blockValidator: SignedBlockValidator[Block, Nothing] = ???
}
