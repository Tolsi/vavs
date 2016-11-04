package ru.tolsi.aobp.blockchain.waves

import com.google.common.primitives.{Bytes, Ints, Longs}
import ru.tolsi.aobp.blockchain.base.{Signable, Signature, Signed, Signer}
import ru.tolsi.aobp.blockchain.waves.crypto.Signature64

private[waves] trait WavesTransactionsSigners {
  this: WavesBlockChain =>

  abstract class WavesSigner[S <: Signable, V, SI <: Signature[V]] extends Signer[WavesBlockChain, S, V, SI]

  implicit object GenesisTransactionSigner extends WavesSigner[GenesisTransaction, Array[Byte], ArraySignature64] {
    val TypeLength = 1
    val TimestampLength = 8
    val AmountLength = 8

    override def sign(tx: GenesisTransaction): Signed[GenesisTransaction, Array[Byte], ArraySignature64] = {
      val typeBytes = Bytes.ensureCapacity(Ints.toByteArray(TransactionType.GenesisTransaction.id), TypeLength, 0)
      val timestampBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.timestamp), TimestampLength, 0)
      val amountBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.amount), AmountLength, 0)
      val data = Bytes.concat(typeBytes, timestampBytes, tx.recipient.address, amountBytes)

      val h = fastHash(data)
      val sing = Bytes.concat(h, h)
      new SignedTransaction[GenesisTransaction](tx) {
        override def signature: ArraySignature64 = Signature64(sing)
      }
    }
  }

  implicit object TransactionSigner extends WavesSigner[T, Array[Byte], ArraySignature64] {
    private def implicitlySign[TX <: T](tx: TX)(implicit signer:WavesSigner[TX, Array[Byte], ArraySignature64]): Signed[TX, Array[Byte], ArraySignature64] = {
      signer.sign(tx)
    }
    override def sign(tx: Transaction): Signed[Transaction, Array[Byte], ArraySignature64] = {
      tx match {
        case tx: GenesisTransaction => implicitlySign(tx)
        case tx: PaymentTransaction => implicitlySign(tx)
        case tx: IssueTransaction => implicitlySign(tx)
        case tx: ReissueTransaction => implicitlySign(tx)
        case tx: TransferTransaction => implicitlySign(tx)
      }
    }
  }
}
