package ru.tolsi.aobp.blockchain.waves.transaction.signer

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.base.{Signature64, Signed}
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesSigner}
import ru.tolsi.aobp.blockchain.waves.transaction.{GenesisTransaction, SignedTransaction, TransactionType}

private[signer] class GenesisTransactionSigner extends WavesSigner[GenesisTransaction, Signature64] {
  val TypeLength = 1
  val TimestampLength = 8
  val AmountLength = 8

  override def sign(tx: GenesisTransaction)(implicit bc: WavesBlockChain): Signed[GenesisTransaction, Signature64] = {
    // todo extract sign creators
    val typeBytes = Bytes.ensureCapacity(Ints.toByteArray(TransactionType.GenesisTransaction.id), TypeLength, 0)
    val timestampBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.timestamp), TimestampLength, 0)
    val amountBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.quantity), AmountLength, 0)
    val sign = Bytes.concat(typeBytes, timestampBytes, tx.recipient.address, amountBytes)

    val h = bc.fastHash(sign)
    val signature = new Signature64(Bytes.concat(h, h))
    SignedTransaction(tx, signature)
  }
}
