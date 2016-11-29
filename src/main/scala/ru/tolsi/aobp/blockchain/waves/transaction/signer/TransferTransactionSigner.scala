package ru.tolsi.aobp.blockchain.waves.transaction.signer

import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, TransferTransaction}
import ru.tolsi.aobp.blockchain.waves.{SignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class TransferTransactionSigner(implicit signCreator: SignCreator[TransferTransaction]) extends WavesSigner[TransferTransaction, WavesSignedTransaction[TransferTransaction], Signature64] {
  override def sign(tx: TransferTransaction)(implicit bc: WavesBlockChain): WavesSignedTransaction[TransferTransaction] = {
    val signature = new Signature64(curve25519.calculateSignature(tx.sender.privateKey.get,
      signCreator.createSign(tx).value))
    WavesSignedTransaction(tx, signature)
  }
}
