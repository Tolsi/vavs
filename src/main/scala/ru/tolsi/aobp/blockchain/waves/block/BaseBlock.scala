package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves._

case class BaseBlock(timestamp: Long,
                     reference: Signature64,
                     baseTarget: Long,
                     generator: Account,
                     generationSignature: Signature32,
                     fee: WavesMoney[Either[Waves.type, Asset]],
                     transactions: Seq[WavesSignedTransaction[WavesTransaction]]) extends WavesBlock {
  val version: Byte = 3
}
