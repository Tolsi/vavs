package ru.tolsi.aobp.blockchain.waves.transaction.signer

import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base.{SignCreator, Signature64}
import ru.tolsi.aobp.blockchain.waves.transaction.{SignedTransaction, TransferTransaction}
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesSigner}

private[signer] class TransferTransactionSigner(implicit signCreator: SignCreator[TransferTransaction]) extends WavesSigner[TransferTransaction, SignedTransaction[TransferTransaction], Signature64] {
  override def sign(tx: TransferTransaction)(implicit bc: WavesBlockChain): SignedTransaction[TransferTransaction] = {
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
      signCreator.createSign(tx).value))
    SignedTransaction(tx, signature)
  }
}
