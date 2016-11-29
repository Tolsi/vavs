package ru.tolsi.aobp.blockchain.waves.transaction.signer

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.waves.transaction.{GenesisTransaction, WavesSignedTransaction}
import ru.tolsi.aobp.blockchain.waves.{SignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class GenesisTransactionSigner(implicit signCreator: SignCreator[GenesisTransaction]) extends WavesSigner[GenesisTransaction, WavesSignedTransaction[GenesisTransaction], Signature64] {
  val TypeLength = 1
  val TimestampLength = 8
  val AmountLength = 8

  override def sign(tx: GenesisTransaction)(implicit bc: WavesBlockChain): WavesSignedTransaction[GenesisTransaction] = {
    val h = bc.fastHash(signCreator.createSign(tx).value)
    val signature = new Signature64(Bytes.concat(h, h))
    WavesSignedTransaction(tx, signature)
  }
}
