package ru.tolsi.aobp.blockchain.waves.network.transport.messages.deserializer.bytes

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.SignedTransaction
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.TransactionMessage
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction

import scala.util.Try

class TransactionMessageDeserializer(signedTransactionSerializer: BytesSerializer[SignedTransaction[WavesTransaction]]) extends NetworkMessageBytesDeserializer[TransactionMessage] {
  override def deserialize(array: Array[Byte]): Try[TransactionMessage] = ???
}
