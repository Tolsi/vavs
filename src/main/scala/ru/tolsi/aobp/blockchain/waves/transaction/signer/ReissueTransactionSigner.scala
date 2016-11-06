package ru.tolsi.aobp.blockchain.waves.transaction.signer

import com.google.common.primitives.{Bytes, Longs}
import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base.{ArraySign, ArraySignCreator, Signature64, Signed}
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesSigner}
import ru.tolsi.aobp.blockchain.waves.transaction.{ReissueTransaction, SignedTransaction, TransactionType}

private[signer] class ReissueTransactionSigner extends WavesSigner[ReissueTransaction, SignedTransaction[ReissueTransaction], Signature64]
  with ArraySignCreator[ReissueTransaction] {
  override def sign(tx: ReissueTransaction)(implicit bc: WavesBlockChain): SignedTransaction[ReissueTransaction] = {
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
      createSign(tx).value))
    SignedTransaction(tx, signature)
  }

  override def createSign(tx: ReissueTransaction): ArraySign = {
    ArraySign(Bytes.concat(Array(TransactionType.ReissueTransaction.id.toByte), tx.sender.publicKey, tx.issue.currency.b.id,
      Longs.toByteArray(tx.quantity),
      Array(booleanWithByte(tx.reissuable)), Longs.toByteArray(tx.fee), Longs.toByteArray(tx.timestamp)))
  }
}
