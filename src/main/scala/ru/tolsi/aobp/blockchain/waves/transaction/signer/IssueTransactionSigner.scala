package ru.tolsi.aobp.blockchain.waves.transaction.signer

import com.google.common.primitives.{Bytes, Longs}
import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base.{ArraySign, ArraySignCreator, Signature64, Signed}
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction.{IssueTransaction, SignedTransaction, TransactionType}

private[signer] class IssueTransactionSigner extends WavesSigner[IssueTransaction, Signature64]
  with ArraySignCreator[IssueTransaction] {
  override def sign(tx: IssueTransaction)(implicit bc: WavesBlockChain): Signed[IssueTransaction, Signature64] = {
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
      createSign(tx).value))
    SignedTransaction(tx, signature)
  }

  override def createSign(tx: IssueTransaction): ArraySign = {
    ArraySign(Bytes.concat(Array(TransactionType.IssueTransaction.id.toByte), tx.sender.publicKey,
      arrayWithSize(tx.name), arrayWithSize(tx.description),
      Longs.toByteArray(tx.quantity), Array(tx.decimals), Array(booleanWithByte(tx.reissuable)),
      Longs.toByteArray(tx.fee), Longs.toByteArray(tx.timestamp)))
  }
}
