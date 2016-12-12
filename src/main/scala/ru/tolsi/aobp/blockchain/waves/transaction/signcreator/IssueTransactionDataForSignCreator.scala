package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import com.google.common.primitives.{Bytes, Longs}
import ru.tolsi.aobp.blockchain.waves.DataForSignCreator
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer._
import ru.tolsi.aobp.blockchain.waves.transaction.{IssueTransaction, TransactionType}

private[signcreator] class IssueTransactionDataForSignCreator extends DataForSignCreator[IssueTransaction] {

  override def serialize(tx: IssueTransaction): Array[Byte] = {
    Bytes.concat(Array(TransactionType.IssueTransaction.id.toByte), tx.sender.publicKey,
      arrayWithKnownSize(tx.name), arrayWithKnownSize(tx.description),
      Longs.toByteArray(tx.quantity), Array(tx.decimals), Array(booleanToByte(tx.reissuable)),
      Longs.toByteArray(tx.fee), Longs.toByteArray(tx.timestamp))
  }
}
