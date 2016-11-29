package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.waves.{Sign, DataForSignCreator}
import ru.tolsi.aobp.blockchain.waves.transaction.{GenesisTransaction, TransactionType}

private[signcreator] class GenesisTransactionDataForSignCreator extends DataForSignCreator[GenesisTransaction] {
  val TypeLength = 1
  val TimestampLength = 8
  val AmountLength = 8

  override def createDataForSign(tx: GenesisTransaction): Sign[GenesisTransaction] = {
    val typeBytes = Bytes.ensureCapacity(Ints.toByteArray(TransactionType.GenesisTransaction.id), TypeLength, 0)
    val timestampBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.timestamp), TimestampLength, 0)
    val amountBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.quantity), AmountLength, 0)
    Sign(Bytes.concat(typeBytes, timestampBytes, tx.recipient.address, amountBytes))
  }
}
