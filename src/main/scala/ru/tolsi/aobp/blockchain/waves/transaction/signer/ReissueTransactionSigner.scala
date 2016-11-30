package ru.tolsi.aobp.blockchain.waves.transaction.signer

import ru.tolsi.aobp.blockchain.waves.transaction.{ReissueTransaction, WavesSignedTransaction}
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class ReissueTransactionSigner(implicit dataForSignCreator: DataForSignCreator[ReissueTransaction]) extends WavesSigner[ReissueTransaction, WavesSignedTransaction[ReissueTransaction], Signature64] {
  override def sign(tx: ReissueTransaction)(implicit bc: WavesBlockChain): WavesSignedTransaction[ReissueTransaction] = {
    val signature = new Signature64(curve25519.calculateSignature(tx.sender.privateKey.get,
      dataForSignCreator.serialize(tx)))
    WavesSignedTransaction(tx, signature)
  }
}
