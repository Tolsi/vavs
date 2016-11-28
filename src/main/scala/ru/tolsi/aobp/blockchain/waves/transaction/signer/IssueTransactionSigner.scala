package ru.tolsi.aobp.blockchain.waves.transaction.signer

import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.waves.transaction.{IssueTransaction, SignedTransaction}
import ru.tolsi.aobp.blockchain.waves.{SignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class IssueTransactionSigner(implicit signCreator: SignCreator[IssueTransaction]) extends WavesSigner[IssueTransaction, SignedTransaction[IssueTransaction], Signature64] {
  override def sign(tx: IssueTransaction)(implicit bc: WavesBlockChain): SignedTransaction[IssueTransaction] = {
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
      signCreator.createSign(tx).value))
    SignedTransaction(tx, signature)
  }
}
