package ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.{SignCreator, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, WavesTransaction}

class SignedTransactionBytesSerializer(txSignCreator: SignCreator[WavesTransaction]) extends BytesSerializer[WavesSignedTransaction[WavesTransaction]] {
  override def serialize(signed: WavesSignedTransaction[WavesTransaction]): Array[Byte] = {
    val sign = txSignCreator.createSign(signed.signed).value
    Bytes.concat(Array(signed.typeId.id.toByte), signed.signature.value, sign)
  }
}
