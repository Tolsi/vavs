package ru.tolsi.aobp.blockchain.waves.transaction.signer

import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, TransferTransaction}
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class TransferTransactionSigner(implicit signCreator: DataForSignCreator[TransferTransaction]) extends WavesSigner[TransferTransaction, WavesSignedTransaction[TransferTransaction], Signature64] {
  override def sign(tx: TransferTransaction)(implicit bc: WavesBlockChain): WavesSignedTransaction[TransferTransaction] = {
    val signature = new Signature64(curve25519.calculateSignature(tx.sender.privateKey.get,
      signCreator.createDataForSign(tx).value))
    WavesSignedTransaction(tx, signature)
  }
}
