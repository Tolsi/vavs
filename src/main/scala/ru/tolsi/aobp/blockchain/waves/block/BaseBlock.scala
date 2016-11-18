package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.base.Signature64
import ru.tolsi.aobp.blockchain.waves.transaction.SignedTransaction
import ru.tolsi.aobp.blockchain.waves._

case class BaseBlock(timestamp: Long,
                     reference: Signature64,
                     baseTarget: Long,
                     generator: WavesBlockChain#AC,
                     generationSignature: Signature32,
                     fee: WavesMoney[Either[Waves.type, Asset]],
                     transactions: Seq[SignedTransaction[WavesBlockChain#T]]) extends WavesBlock {
  val version: Byte = 3
}
