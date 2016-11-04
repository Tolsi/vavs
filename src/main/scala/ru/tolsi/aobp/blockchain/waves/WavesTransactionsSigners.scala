package ru.tolsi.aobp.blockchain.waves

import com.google.common.primitives.{Bytes, Ints, Longs, Shorts}
import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.crypto.Signature64

private[waves] trait WavesTransactionsSigners {
  this: WavesBlockChain =>

  abstract class WavesSigner[S <: Signable with WithByteArraySing, V, SI <: Signature[V]] extends Signer[WavesBlockChain, S, V, SI] {
    // todo move to bytes serializer
    protected def arrayWithSize(b: Array[Byte]): Array[Byte] = Shorts.toByteArray(b.length.toShort) ++ b

    protected def booleanWithByte(b: Boolean): Byte = (if (b) 1 else 0).toByte

    protected def writeArrayOption(a: Option[Array[Byte]]): Array[Byte] = a.map(a => (1: Byte) +: a).getOrElse(Array(0: Byte))
  }

  implicit object GenesisTransactionSigner extends WavesSigner[GenesisTransaction, Array[Byte], ArraySignature64] {
    val TypeLength = 1
    val TimestampLength = 8
    val AmountLength = 8

    override def sign(tx: GenesisTransaction): Signed[GenesisTransaction, Array[Byte], ArraySignature64] = {
      val typeBytes = Bytes.ensureCapacity(Ints.toByteArray(TransactionType.GenesisTransaction.id), TypeLength, 0)
      val timestampBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.timestamp), TimestampLength, 0)
      val amountBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.amount), AmountLength, 0)
      val sign = Bytes.concat(typeBytes, timestampBytes, tx.recipient.address, amountBytes)

      val h = fastHash(sign)
      val signature = Signature64(Bytes.concat(h, h))
      SignedTransaction(tx, signature)
    }
  }

  implicit object PaymentTransactionSigner extends WavesSigner[PaymentTransaction, Array[Byte], ArraySignature64]
    with SignCreator[PaymentTransaction, Array[Byte], ArraySign] {
    override def sign(tx: PaymentTransaction): Signed[PaymentTransaction, Array[Byte], ArraySignature64] = {
      val signature = Signature64(createSign(tx).value)
      SignedTransaction(tx, signature)
    }

    override def createSign(tx: PaymentTransaction): ArraySign = {
      import tx._
      val typeBytes = Ints.toByteArray(TransactionType.PaymentTransaction.id)
      val timestampBytes = Longs.toByteArray(timestamp)
      val amountBytes = Longs.toByteArray(amount)
      val feeBytes = Longs.toByteArray(fee)
      ArraySign(Bytes.concat(typeBytes, timestampBytes, sender.publicKey, recipient.address, amountBytes, feeBytes))
    }
  }

  implicit object IssueTransactionSigner extends WavesSigner[IssueTransaction, Array[Byte], ArraySignature64]
    with SignCreator[IssueTransaction, Array[Byte], ArraySign] {
    override def sign(tx: IssueTransaction): Signed[IssueTransaction, Array[Byte], ArraySignature64] = {
      val signature = Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
        createSign(tx).value))
      SignedTransaction(tx, signature)
    }

    override def createSign(tx: IssueTransaction): ArraySign = {
      import tx._
      ArraySign(Bytes.concat(Array(TransactionType.IssueTransaction.id.toByte), sender.publicKey,
        arrayWithSize(name), arrayWithSize(description),
        Longs.toByteArray(amount), Array(decimals), Array(booleanWithByte(reissuable)),
        Longs.toByteArray(fee), Longs.toByteArray(timestamp)))
    }
  }

  implicit object ReissueTransactionSigner extends WavesSigner[ReissueTransaction, Array[Byte], ArraySignature64]
    with SignCreator[ReissueTransaction, Array[Byte], ArraySign] {
    override def sign(tx: ReissueTransaction): Signed[ReissueTransaction, Array[Byte], ArraySignature64] = {
      val signature = Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
        createSign(tx).value))
      SignedTransaction(tx, signature)
    }

    override def createSign(tx: ReissueTransaction): ArraySign = {
      import tx._
      ArraySign(Bytes.concat(Array(TransactionType.ReissueTransaction.id.toByte), sender.publicKey, tx.issue.currency.b.id,
        Longs.toByteArray(amount),
        Array(booleanWithByte(reissuable)), Longs.toByteArray(fee), Longs.toByteArray(timestamp)))
    }
  }

  implicit object TransferTransactionSigner extends WavesSigner[TransferTransaction, Array[Byte], ArraySignature64]
    with SignCreator[TransferTransaction, Array[Byte], ArraySign] {
    override def sign(tx: TransferTransaction): Signed[TransferTransaction, Array[Byte], ArraySignature64] = {
      val signature = Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
        createSign(tx).value))
      SignedTransaction(tx, signature)
    }

    override def createSign(tx: TransferTransaction): ArraySign = {
      import tx._
      val timestampBytes = Longs.toByteArray(timestamp)
      // todo as serializer
      val assetIdBytes = writeArrayOption(transfer.currency.fold(_ => None, a => Some(a.id)))
      val amountBytes = Longs.toByteArray(amount)
      val feeAssetBytes = writeArrayOption(feeMoney.currency.fold(_ => None, a => Some(a.id)))
      val feeBytes = Longs.toByteArray(fee)

      ArraySign(Bytes.concat(Array(TransactionType.TransferTransaction.id.toByte), sender.publicKey, assetIdBytes, feeAssetBytes,
        timestampBytes, amountBytes, feeBytes,
        recipient.address, arrayWithSize(attachment)))
    }
  }

  implicit object TransactionSigner extends WavesSigner[T, Array[Byte], ArraySignature64] {
    private def implicitlySign[TX <: T](tx: TX)(implicit signer: WavesSigner[TX, Array[Byte], ArraySignature64]): Signed[TX, Array[Byte], ArraySignature64] = {
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
