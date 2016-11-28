package ru.tolsi.aobp.blockchain.waves.transaction.signer

import ru.tolsi.aobp.blockchain.waves.transaction.{PaymentTransaction, SignedTransaction}
import ru.tolsi.aobp.blockchain.waves.{SignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class PaymentTransactionSigner(implicit signCreator: SignCreator[PaymentTransaction]) extends WavesSigner[PaymentTransaction, SignedTransaction[PaymentTransaction], Signature64] {
  override def sign(tx: PaymentTransaction)(implicit bc: WavesBlockChain): SignedTransaction[PaymentTransaction] = {
    val signature = new Signature64(signCreator.createSign(tx).value)
    SignedTransaction(tx, signature)
  }
}
