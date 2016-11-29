package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import com.google.common.primitives.{Bytes, Longs}
import ru.tolsi.aobp.blockchain.waves.{Sign, DataForSignCreator}
import ru.tolsi.aobp.blockchain.waves.serializer.BytesUtils._
import ru.tolsi.aobp.blockchain.waves.transaction.{ReissueTransaction, TransactionType}

private[signcreator] class ReissueTransactionDataForSignCreator extends DataForSignCreator[ReissueTransaction] {
  override def createDataForSign(tx: ReissueTransaction): Sign[ReissueTransaction] = {
    Sign(Bytes.concat(Array(TransactionType.ReissueTransaction.id.toByte), tx.sender.publicKey, tx.issue.currency.value.id,
      Longs.toByteArray(tx.quantity),
      Array(booleanToByte(tx.reissuable)), Longs.toByteArray(tx.fee), Longs.toByteArray(tx.timestamp)))
  }
}
