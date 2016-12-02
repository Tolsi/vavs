package ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.transaction.IssueTransaction
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, SignedTransaction}

private[bytes] class SignedIssueTransactionBytesSerializer(dataForSignCreator: DataForSignCreator[IssueTransaction]) extends BytesSerializer[SignedTransaction[IssueTransaction]] {
  override def serialize(tx: SignedTransaction[IssueTransaction]): Array[Byte] = {
    Bytes.concat(Array(tx.typeId.id.toByte), tx.signature.value, dataForSignCreator.serialize(tx.signed))

  }
}
