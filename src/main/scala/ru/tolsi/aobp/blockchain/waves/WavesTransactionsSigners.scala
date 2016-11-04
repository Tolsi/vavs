package ru.tolsi.aobp.blockchain.waves

import com.google.common.primitives.{Bytes, Ints, Longs, Shorts}
import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base._

private[waves] trait WavesTransactionsSigners {
  this: WavesBlockChain =>

  abstract class WavesSigner[S <: Signable with WithByteArraySing, SI <: Signature[Array[Byte]]] extends Signer[WavesBlockChain, S, SI] {
    // todo move to bytes serializer
    protected def arrayWithSize(b: Array[Byte]): Array[Byte] = Shorts.toByteArray(b.length.toShort) ++ b

    protected def booleanWithByte(b: Boolean): Byte = (if (b) 1 else 0).toByte

    protected def writeArrayOption(a: Option[Array[Byte]]): Array[Byte] = a.map(a => (1: Byte) +: a).getOrElse(Array(0: Byte))
  }

  implicit object GenesisTransactionSigner extends WavesSigner[GenesisTransaction, Signature64] {
    val TypeLength = 1
    val TimestampLength = 8
    val AmountLength = 8

    override def sign(tx: GenesisTransaction): Signed[GenesisTransaction, Signature64] = {
      val typeBytes = Bytes.ensureCapacity(Ints.toByteArray(TransactionType.GenesisTransaction.id), TypeLength, 0)
      val timestampBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.timestamp), TimestampLength, 0)
      val amountBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.quantity), AmountLength, 0)
      val sign = Bytes.concat(typeBytes, timestampBytes, tx.recipient.address, amountBytes)

      val h = fastHash(sign)
      val signature = new Signature64(Bytes.concat(h, h))
      SignedTransaction(tx, signature)
    }
  }

  implicit object PaymentTransactionSigner extends WavesSigner[PaymentTransaction, Signature64]
    with ArraySignCreator[PaymentTransaction] {
    override def sign(tx: PaymentTransaction): Signed[PaymentTransaction, Signature64] = {
      val signature = new Signature64(createSign(tx).value)
      SignedTransaction(tx, signature)
    }

    override def createSign(tx: PaymentTransaction): ArraySign = {
      import tx._
      val typeBytes = Ints.toByteArray(TransactionType.PaymentTransaction.id)
      val timestampBytes = Longs.toByteArray(timestamp)
      val amountBytes = Longs.toByteArray(quantity)
      val feeBytes = Longs.toByteArray(fee)
      ArraySign(Bytes.concat(typeBytes, timestampBytes, sender.publicKey, recipient.address, amountBytes, feeBytes))
    }
  }

  implicit object IssueTransactionSigner extends WavesSigner[IssueTransaction, Signature64]
    with ArraySignCreator[IssueTransaction] {
    override def sign(tx: IssueTransaction): Signed[IssueTransaction, Signature64] = {
      val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
        createSign(tx).value))
      SignedTransaction(tx, signature)
    }

    override def createSign(tx: IssueTransaction): ArraySign = {
      import tx._
      ArraySign(Bytes.concat(Array(TransactionType.IssueTransaction.id.toByte), sender.publicKey,
        arrayWithSize(name), arrayWithSize(description),
        Longs.toByteArray(quantity), Array(decimals), Array(booleanWithByte(reissuable)),
        Longs.toByteArray(fee), Longs.toByteArray(timestamp)))
    }
  }

  implicit object ReissueTransactionSigner extends WavesSigner[ReissueTransaction, Signature64]
    with ArraySignCreator[ReissueTransaction] {
    override def sign(tx: ReissueTransaction): Signed[ReissueTransaction, Signature64] = {
      val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
        createSign(tx).value))
      SignedTransaction(tx, signature)
    }

    override def createSign(tx: ReissueTransaction): ArraySign = {
      import tx._
      ArraySign(Bytes.concat(Array(TransactionType.ReissueTransaction.id.toByte), sender.publicKey, tx.issue.currency.b.id,
        Longs.toByteArray(quantity),
        Array(booleanWithByte(reissuable)), Longs.toByteArray(fee), Longs.toByteArray(timestamp)))
    }
  }

  implicit object TransferTransactionSigner extends WavesSigner[TransferTransaction, Signature64]
    with ArraySignCreator[TransferTransaction] {
    override def sign(tx: TransferTransaction): Signed[TransferTransaction, Signature64] = {
      val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
        createSign(tx).value))
      SignedTransaction(tx, signature)
    }

    override def createSign(tx: TransferTransaction): ArraySign = {
      import tx._
      val timestampBytes = Longs.toByteArray(timestamp)
      // todo as serializer
      val assetIdBytes = writeArrayOption(transfer.currency.fold(_ => None, a => Some(a.id)))
      val amountBytes = Longs.toByteArray(quantity)
      val feeAssetBytes = writeArrayOption(feeMoney.currency.fold(_ => None, a => Some(a.id)))
      val feeBytes = Longs.toByteArray(fee)

      ArraySign(Bytes.concat(Array(TransactionType.TransferTransaction.id.toByte), sender.publicKey, assetIdBytes, feeAssetBytes,
        timestampBytes, amountBytes, feeBytes,
        recipient.address, arrayWithSize(attachment)))
    }
  }

  implicit object TransactionSigner extends WavesSigner[T, Signature64] {
    private def implicitlySign[TX <: T](tx: TX)(implicit signer: WavesSigner[TX, Signature64]): Signed[TX, Signature64] = {
      signer.sign(tx)
    }

    override def sign(tx: WavesTransaction): Signed[WavesTransaction, Signature64] = {
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
