package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.base.{BlockChainSignedBlock, Signature32, Signature64, Signed}
import ru.tolsi.aobp.blockchain.waves.transaction.SignedTransaction
import ru.tolsi.aobp.blockchain.waves.{Asset, Waves, WavesBlockChain, WavesMoney}

case class SignedBlock[BL <: WavesBlockChain#B](block: BL, signature: Signature64) extends WavesBlock with BlockChainSignedBlock[WavesBlockChain, BL, Signature64] {
  override def version: Byte = block.version

  override def timestamp: Long = block.timestamp

  override def reference: Signature64 = block.reference

  override def transactions: Seq[SignedTransaction[WavesBlockChain#T]] = block.transactions

  override def baseTarget: Long = block.baseTarget

  override def generator: WavesBlockChain#AC = block.generator

  override def generationSignature: Signature32 = block.generationSignature

  override def signed: BL = block

  override def fee: WavesMoney[Either[Waves.type, Asset]] = block.fee
}
