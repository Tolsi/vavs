package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{Signed, ValidatorOnBlockChain}

private[waves] trait WavesBlocks {
  this: WavesBlockChain =>

  abstract class WavesBlock extends BlockChainBlock {
    override type Id = ArraySignature32

    val version: Byte
    val timestamp: Long
    val reference: ArraySignature64

    def transactions: Seq[Signed[Transaction, Array[Byte], ArraySignature64]]

    val baseTarget: Long
    val generatorSignature: ArraySignature32
  }

  class GenesisBlock(val timestamp: Long,
                     val reference: ArraySignature64,
                     val transactions: Seq[Signed[Transaction, Array[Byte], ArraySignature64]],
                     val baseTarget: Long,
                     val generatorSignature: ArraySignature32) extends WavesBlock {
    val version: Byte = 2
  }


  class Block(val timestamp: Long,
              val reference: ArraySignature64,
              val transactions: Seq[Signed[Transaction, Array[Byte], ArraySignature64]],
              val baseTarget: Long,
              val generatorSignature: ArraySignature32) extends WavesBlock {
    val version: Byte = 3
  }

}
