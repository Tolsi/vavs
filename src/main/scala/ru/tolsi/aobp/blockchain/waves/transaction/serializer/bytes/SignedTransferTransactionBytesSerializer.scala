package ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.transaction.TransferTransaction
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, SignedTransaction}

private[bytes] class SignedTransferTransactionBytesSerializer(dataForSignCreator: DataForSignCreator[TransferTransaction]) extends BytesSerializer[SignedTransaction[TransferTransaction]] {
  override def serialize(tx: SignedTransaction[TransferTransaction]): Array[Byte] = {
    Bytes.concat(Array(tx.typeId.id.toByte), tx.signature.value, dataForSignCreator.serialize(tx.signed))
  }
}
