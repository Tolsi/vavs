package ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.transaction.ReissueTransaction
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, SignedTransaction}

private[bytes] class SignedReissueTransactionBytesSerializer(dataForSignCreator: DataForSignCreator[ReissueTransaction]) extends BytesSerializer[SignedTransaction[ReissueTransaction]] {
  override def serialize(tx: SignedTransaction[ReissueTransaction]): Array[Byte] = {
    Bytes.concat(Array(tx.typeId.id.toByte), tx.signature.value, dataForSignCreator.serialize(tx.signed))
  }
}
