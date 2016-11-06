package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.base.{Signature32, Signature64, Signed}
import ru.tolsi.aobp.blockchain.waves.transaction.{SignedTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves.{Asset, Waves, WavesBlockChain, WavesMoney}

case class GenesisBlock(timestamp: Long,
                        reference: Signature64,
                        baseTarget: Long,
                        generator: WavesBlockChain#AC,
                        generationSignature: Signature32,
                        fee: WavesMoney[Either[Waves.type, Asset]],
                        transactions: Seq[SignedTransaction[WavesBlockChain#T]]
                       ) extends WavesBlock {
  val version: Byte = 2
}
