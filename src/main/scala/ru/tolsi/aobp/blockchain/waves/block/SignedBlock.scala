package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.waves.transaction.{SignedTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves._

case class SignedBlock[BL <: WavesBlock](block: BL, signature: Signature64) extends WavesBlock with BlockChainSignedBlock[BL, Signature64] {
  override def version: Byte = block.version

  override def timestamp: Long = block.timestamp

  override def reference: Signature64 = block.reference

  override def transactions: Seq[SignedTransaction[WavesTransaction]] = block.transactions

  override def baseTarget: Long = block.baseTarget

  override def generator: Account = block.generator

  override def generationSignature: Signature32 = block.generationSignature

  override def signed: BL = block

  override def fee: WavesMoney[Either[Waves.type, Asset]] = block.fee
}
