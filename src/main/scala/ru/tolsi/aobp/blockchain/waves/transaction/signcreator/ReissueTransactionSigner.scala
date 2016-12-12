package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import com.google.common.primitives.{Bytes, Longs}
import ru.tolsi.aobp.blockchain.waves.DataForSignCreator
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer._
import ru.tolsi.aobp.blockchain.waves.transaction.{ReissueTransaction, TransactionType}

private[signcreator] class ReissueTransactionDataForSignCreator extends DataForSignCreator[ReissueTransaction] {
  override def serialize(tx: ReissueTransaction): Array[Byte] = {
    Bytes.concat(Array(TransactionType.ReissueTransaction.id.toByte), tx.sender.publicKey, tx.issue.currency.value.id,
      Longs.toByteArray(tx.quantity),
      Array(booleanToByte(tx.reissuable)), Longs.toByteArray(tx.fee), Longs.toByteArray(tx.timestamp))
  }
}
