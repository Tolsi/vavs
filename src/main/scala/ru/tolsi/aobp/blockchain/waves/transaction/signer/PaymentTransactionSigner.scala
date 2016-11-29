package ru.tolsi.aobp.blockchain.waves.transaction.signer

import ru.tolsi.aobp.blockchain.waves.transaction.{PaymentTransaction, WavesSignedTransaction}
import ru.tolsi.aobp.blockchain.waves.{SignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class PaymentTransactionSigner(implicit signCreator: SignCreator[PaymentTransaction]) extends WavesSigner[PaymentTransaction, WavesSignedTransaction[PaymentTransaction], Signature64] {
  override def sign(tx: PaymentTransaction)(implicit bc: WavesBlockChain): WavesSignedTransaction[PaymentTransaction] = {
    val signature = new Signature64(signCreator.createSign(tx).value)
    WavesSignedTransaction(tx, signature)
  }
}
