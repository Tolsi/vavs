package ru.tolsi.aobp.blockchain.waves.transaction.signer

import ru.tolsi.aobp.blockchain.waves.transaction.{IssueTransaction, WavesSignedTransaction}
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class IssueTransactionSigner(implicit dataForSignCreator: DataForSignCreator[IssueTransaction]) extends WavesSigner[IssueTransaction, WavesSignedTransaction[IssueTransaction], Signature64] {
  override def sign(tx: IssueTransaction)(implicit bc: WavesBlockChain): WavesSignedTransaction[IssueTransaction] = {
    val signature = new Signature64(curve25519.calculateSignature(tx.sender.privateKey.get,
      dataForSignCreator.serialize(tx)))
    WavesSignedTransaction(tx, signature)
  }
}
