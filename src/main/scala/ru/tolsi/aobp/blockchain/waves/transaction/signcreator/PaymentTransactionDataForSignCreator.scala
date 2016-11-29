package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.waves.{Sign, DataForSignCreator}
import ru.tolsi.aobp.blockchain.waves.transaction.{PaymentTransaction, TransactionType}

private[signcreator] class PaymentTransactionDataForSignCreator extends DataForSignCreator[PaymentTransaction] {
  override def createDataForSign(tx: PaymentTransaction): Sign[PaymentTransaction] = {
    val typeBytes = Ints.toByteArray(TransactionType.PaymentTransaction.id)
    val timestampBytes = Longs.toByteArray(tx.timestamp)
    val amountBytes = Longs.toByteArray(tx.quantity)
    val feeBytes = Longs.toByteArray(tx.fee)
    Sign(Bytes.concat(typeBytes, timestampBytes, tx.sender.publicKey, tx.recipient.address, amountBytes, feeBytes))
  }
}
