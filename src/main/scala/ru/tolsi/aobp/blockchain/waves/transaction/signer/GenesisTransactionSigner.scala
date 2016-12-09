package ru.tolsi.aobp.blockchain.waves.transaction.signer

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.waves.transaction.{GenesisTransaction, WavesSignedTransaction}
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, Signature64, WavesBlockChain, WavesSigner}
import scorex.crypto.hash.Blake256

private[signer] class GenesisTransactionSigner(implicit dataForSignCreator: DataForSignCreator[GenesisTransaction]) extends WavesSigner[GenesisTransaction, WavesSignedTransaction[GenesisTransaction], Signature64] {
  val TypeLength = 1
  val TimestampLength = 8
  val AmountLength = 8

  override def sign(tx: GenesisTransaction)(implicit bc: WavesBlockChain): WavesSignedTransaction[GenesisTransaction] = {
    val h = Blake256(dataForSignCreator.serialize(tx))
    val signature = new Signature64(Bytes.concat(h, h))
    WavesSignedTransaction(tx, signature)
  }
}
