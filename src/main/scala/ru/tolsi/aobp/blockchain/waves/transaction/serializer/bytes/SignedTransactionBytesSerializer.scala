package ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.{SignCreator, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.transaction.{SignedTransaction, WavesTransaction}

class SignedTransactionBytesSerializer(implicit arraySignCreator: SignCreator[WavesTransaction]) extends BytesSerializer[SignedTransaction[WavesTransaction]] {
  override def serialize(signed: SignedTransaction[WavesTransaction]): Array[Byte] = {
    val sign = arraySignCreator.createSign(signed.signed).value
    Bytes.concat(Array(signed.typeId.id.toByte), signed.signature.value, sign)
  }
}
