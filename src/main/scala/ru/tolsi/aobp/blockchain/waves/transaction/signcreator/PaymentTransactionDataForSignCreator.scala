package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.waves.DataForSignCreator
import ru.tolsi.aobp.blockchain.waves.transaction.{PaymentTransaction, TransactionType}

private[signcreator] class PaymentTransactionDataForSignCreator extends DataForSignCreator[PaymentTransaction] {
  override def serialize(tx: PaymentTransaction): Array[Byte] = {
    val typeBytes = Ints.toByteArray(TransactionType.PaymentTransaction.id)
    val timestampBytes = Longs.toByteArray(tx.timestamp)
    val amountBytes = Longs.toByteArray(tx.quantity)
    val feeBytes = Longs.toByteArray(tx.fee)
    Bytes.concat(typeBytes, timestampBytes, tx.sender.publicKey, tx.recipient.address, amountBytes, feeBytes)
  }
}
