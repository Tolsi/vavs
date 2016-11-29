package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves._

case class WavesSignedBlock[+BL <: WavesBlock](block: BL, signature: Signature64) extends WavesBlock with Signed[WavesBlock, Signature64] {
  override val version: Byte = block.version

  override val timestamp: Long = block.timestamp

  override val reference: Signature64 = block.reference

  override val transactions: Seq[WavesSignedTransaction[WavesTransaction]] = block.transactions

  override val baseTarget: Long = block.baseTarget

  override val generator: Account = block.generator

  override val generationSignature: Signature32 = block.generationSignature

  override val signed: BL = block

  override val fee: WavesMoney[Either[Waves.type, Asset]] = block.fee
}
