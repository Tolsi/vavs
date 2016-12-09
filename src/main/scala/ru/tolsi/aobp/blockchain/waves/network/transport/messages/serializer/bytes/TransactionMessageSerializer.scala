package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer._
import ru.tolsi.aobp.blockchain.waves.SignedTransaction
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.TransactionMessage
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction

class TransactionMessageSerializer(signedTransactionSerializer: BytesSerializer[SignedTransaction[WavesTransaction]])
  extends NetworkMessageSerializer[TransactionMessage] {
  override def serialize(transaction: TransactionMessage): Array[Byte] = {
    val magicBytes = NetworkMessage.MagicBytes
    val contentId = transaction.contentId
    val txBytes = signedTransactionSerializer.serialize(transaction.tx)
    val txBytesLength = txBytes.length
    val payloadChecksum = calculateDataChecksum(txBytes)
    val packetLength = 17 + txBytesLength
    Bytes.concat(
      intBytesEnsureCapacity(packetLength),
      magicBytes,
      Array(contentId),
      intBytesEnsureCapacity(txBytesLength),
      payloadChecksum,
      txBytes
    )
  }
}
