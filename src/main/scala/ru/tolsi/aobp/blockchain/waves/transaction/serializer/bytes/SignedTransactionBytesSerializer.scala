package ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.transaction.{GenesisTransaction, IssueTransaction, PaymentTransaction, ReissueTransaction, WavesSignedTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, SignedTransaction}

class SignedTransactionBytesSerializer(dataForSignCreator: DataForSignCreator[GenesisTransaction]) extends BytesSerializer[WavesSignedTransaction[WavesTransaction]] {
  override def serialize(signed: WavesSignedTransaction[WavesTransaction]): Array[Byte] = {
    signed.signed match {
      case tx: GenesisTransaction =>
        dataForSignCreator.serialize(tx)
      //todo ???
      case tx: PaymentTransaction =>
        implicitly[BytesSerializer[SignedTransaction[PaymentTransaction]]].serialize(tx.asInstanceOf[SignedTransaction[PaymentTransaction]])
      case tx: IssueTransaction =>
        implicitly[BytesSerializer[SignedTransaction[IssueTransaction]]].serialize(tx.asInstanceOf[SignedTransaction[IssueTransaction]])
      case tx: ReissueTransaction =>
        implicitly[BytesSerializer[SignedTransaction[ReissueTransaction]]].serialize(tx.asInstanceOf[SignedTransaction[ReissueTransaction]])
    }
  }
}
