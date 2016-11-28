package ru.tolsi.aobp.blockchain.waves.transaction.signer

import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.waves.transaction.{ReissueTransaction, SignedTransaction}
import ru.tolsi.aobp.blockchain.waves.{SignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class ReissueTransactionSigner(implicit signCreator: SignCreator[ReissueTransaction]) extends WavesSigner[ReissueTransaction, SignedTransaction[ReissueTransaction], Signature64] {
  override def sign(tx: ReissueTransaction)(implicit bc: WavesBlockChain): SignedTransaction[ReissueTransaction] = {
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
      signCreator.createSign(tx).value))
    SignedTransaction(tx, signature)
  }
}
