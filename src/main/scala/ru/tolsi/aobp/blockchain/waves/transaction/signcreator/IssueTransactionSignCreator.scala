package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import com.google.common.primitives.{Bytes, Longs}
import ru.tolsi.aobp.blockchain.base.{Sign, SignCreator}
import ru.tolsi.aobp.blockchain.waves.serializer.BytesUtils._
import ru.tolsi.aobp.blockchain.waves.transaction.{IssueTransaction, TransactionType}

private[signcreator] class IssueTransactionSignCreator extends SignCreator[IssueTransaction] {

  override def createSign(tx: IssueTransaction): Sign[IssueTransaction] = {
    Sign(Bytes.concat(Array(TransactionType.IssueTransaction.id.toByte), tx.sender.publicKey,
      arrayWithSize(tx.name), arrayWithSize(tx.description),
      Longs.toByteArray(tx.quantity), Array(tx.decimals), Array(booleanToByte(tx.reissuable)),
      Longs.toByteArray(tx.fee), Longs.toByteArray(tx.timestamp)))
  }
}
