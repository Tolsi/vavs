package ru.tolsi.aobp.blockchain.waves.transaction.signer

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.base.{ArraySign, ArraySignCreator, Signature64, Signed}
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesSigner}
import ru.tolsi.aobp.blockchain.waves.transaction.{PaymentTransaction, SignedTransaction, TransactionType}

private[signer] class PaymentTransactionSigner extends WavesSigner[PaymentTransaction, SignedTransaction[PaymentTransaction], Signature64]
  with ArraySignCreator[PaymentTransaction] {
  override def sign(tx: PaymentTransaction)(implicit bc: WavesBlockChain): SignedTransaction[PaymentTransaction] = {
    val signature = new Signature64(createSign(tx).value)
    SignedTransaction(tx, signature)
  }

  override def createSign(tx: PaymentTransaction): ArraySign = {
    val typeBytes = Ints.toByteArray(TransactionType.PaymentTransaction.id)
    val timestampBytes = Longs.toByteArray(tx.timestamp)
    val amountBytes = Longs.toByteArray(tx.quantity)
    val feeBytes = Longs.toByteArray(tx.fee)
    ArraySign(Bytes.concat(typeBytes, timestampBytes, tx.sender.publicKey, tx.recipient.address, amountBytes, feeBytes))
  }
}
