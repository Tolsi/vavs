package ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes

import com.google.common.primitives.{Bytes, Longs}
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.SignedTransaction
import ru.tolsi.aobp.blockchain.waves.transaction.{PaymentTransaction, TransactionType}

private[bytes] class SignedPaymentTransactionBytesSerializer extends BytesSerializer[SignedTransaction[PaymentTransaction]] {
  override def serialize(tx: SignedTransaction[PaymentTransaction]): Array[Byte] = {
    val typeBytes = TransactionType.PaymentTransaction.id.toByte
    val timestampBytes = Longs.toByteArray(tx.timestamp)
    val amountBytes = Longs.toByteArray(tx.quantity)
    val feeBytes = Longs.toByteArray(tx.fee)
    Bytes.concat(Array(typeBytes), timestampBytes, tx.sender.publicKey, tx.recipient.address, amountBytes, feeBytes, tx.signature.value)
  }
}
