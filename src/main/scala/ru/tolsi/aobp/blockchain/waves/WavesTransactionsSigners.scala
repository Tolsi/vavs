package ru.tolsi.aobp.blockchain.waves

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.base.{Signed, Signer}
import ru.tolsi.aobp.blockchain.waves.crypto.Signature64

private[waves] trait WavesTransactionsSigners {
  this: WavesBlockChain =>

  object GenesisTransactionSigner extends Signer[WavesBlockChain, GenesisTransaction, Array[Byte], ArraySignature64] {
    val TypeLength = 1
    val TimestampLength = 8
    val AmountLength = 8

    override def sign(tx: GenesisTransaction)(implicit blockChain: WavesBlockChain): Signed[GenesisTransaction, Array[Byte], ArraySignature64] = {
      val typeBytes = Bytes.ensureCapacity(Ints.toByteArray(TransactionType.GenesisTransaction.id), TypeLength, 0)
      val timestampBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.timestamp), TimestampLength, 0)
      val amountBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.amount), AmountLength, 0)
      val data = Bytes.concat(typeBytes, timestampBytes, tx.recipient.address, amountBytes)

      val h = blockChain.fastHash(data)
      val sing = Bytes.concat(h, h)
      new SignedTransaction[GenesisTransaction](tx) {
        override def signature: ArraySignature64 = Signature64(sing)
      }
    }
  }

}
