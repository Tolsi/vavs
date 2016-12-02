package ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.transaction.{GenesisTransaction, IssueTransaction, PaymentTransaction, ReissueTransaction, WavesSignedTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, SignedTransaction}

class SignedTransactionBytesSerializer(dataForSignCreator: DataForSignCreator[GenesisTransaction]) extends BytesSerializer[WavesSignedTransaction[WavesTransaction]] {
  override def serialize(signed: WavesSignedTransaction[WavesTransaction]): Array[Byte] = {
    signed match {
      case tx: SignedTransaction[GenesisTransaction] => dataForSignCreator.serialize(tx.signed)
      case tx: SignedTransaction[PaymentTransaction] => implicitly[BytesSerializer[SignedTransaction[PaymentTransaction]]].serialize(tx)
      case tx: SignedTransaction[IssueTransaction] => implicitly[BytesSerializer[SignedTransaction[IssueTransaction]]].serialize(tx)
      case tx: SignedTransaction[ReissueTransaction] => implicitly[BytesSerializer[SignedTransaction[ReissueTransaction]]].serialize(tx)
    }
  }
}
