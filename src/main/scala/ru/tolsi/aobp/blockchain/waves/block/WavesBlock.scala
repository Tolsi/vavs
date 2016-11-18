package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves._


abstract class WavesBlock extends BlockChainBlock[WavesBlockChain] {
  override type Id = Signature64

  def version: Byte

  def timestamp: Long

  def reference: Signature64

  def baseTarget: Long

  // fasthash(lastBlockData.generationSignature ++ generator.publicKey)
  def generationSignature: Signature32

  def fee: WavesMoney[Either[Waves.type, Asset]]

  def transactions: Seq[WavesBlockChain#ST[WavesBlockChain#T]]

  def generator: WavesBlockChain#AC
}
