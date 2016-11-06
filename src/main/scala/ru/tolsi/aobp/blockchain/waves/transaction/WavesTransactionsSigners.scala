package ru.tolsi.aobp.blockchain.waves.transaction

import com.google.common.primitives.{Bytes, Ints, Longs}
import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesSigner}


class GenesisTransactionSigner extends WavesSigner[GenesisTransaction, Signature64] {
  val TypeLength = 1
  val TimestampLength = 8
  val AmountLength = 8

  override def sign(tx: GenesisTransaction)(implicit bc: WavesBlockChain): Signed[GenesisTransaction, Signature64] = {
    val typeBytes = Bytes.ensureCapacity(Ints.toByteArray(TransactionType.GenesisTransaction.id), TypeLength, 0)
    val timestampBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.timestamp), TimestampLength, 0)
    val amountBytes = Bytes.ensureCapacity(Longs.toByteArray(tx.quantity), AmountLength, 0)
    val sign = Bytes.concat(typeBytes, timestampBytes, tx.recipient.address, amountBytes)

    val h = bc.fastHash(sign)
    val signature = new Signature64(Bytes.concat(h, h))
    SignedTransaction(tx, signature)
  }
}

object PaymentTransactionSigner extends WavesSigner[PaymentTransaction, Signature64]
  with ArraySignCreator[PaymentTransaction] {
  override def sign(tx: PaymentTransaction): Signed[PaymentTransaction, Signature64] = {
    val signature = new Signature64(createSign(tx).value)
    SignedTransaction(tx, signature)
  }

  override def createSign(tx: PaymentTransaction): ArraySign = {
    val typeBytes = Ints.toByteArray(TransactionType.PaymentTransaction.id)
    val timestampBytes = Longs.toByteArray(tx.timestamp)
    val amountBytes = Longs.toByteArray(tx.quantity)
    val feeBytes = Longs.toByteArray(tx.fee)
    ArraySign(Bytes.concat(typeBytes, timestampBytes, tx.sender.publicKey, tx.recipient.address, amountBytes, feeBytes))
  }
}

object IssueTransactionSigner extends WavesSigner[IssueTransaction, Signature64]
  with ArraySignCreator[IssueTransaction] {
  override def sign(tx: IssueTransaction)(implicit bc: WavesBlockChain): Signed[IssueTransaction, Signature64] = {
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
      createSign(tx).value))
    SignedTransaction(tx, signature)
  }

  override def createSign(tx: IssueTransaction): ArraySign = {
    ArraySign(Bytes.concat(Array(TransactionType.IssueTransaction.id.toByte), tx.sender.publicKey,
      arrayWithSize(tx.name), arrayWithSize(tx.description),
      Longs.toByteArray(tx.quantity), Array(tx.decimals), Array(booleanWithByte(tx.reissuable)),
      Longs.toByteArray(tx.fee), Longs.toByteArray(tx.timestamp)))
  }
}

object ReissueTransactionSigner extends WavesSigner[ReissueTransaction, Signature64]
  with ArraySignCreator[ReissueTransaction] {
  override def sign(tx: ReissueTransaction)(implicit bc: WavesBlockChain): Signed[ReissueTransaction, Signature64] = {
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
      createSign(tx).value))
    SignedTransaction(tx, signature)
  }

  override def createSign(tx: ReissueTransaction): ArraySign = {
    ArraySign(Bytes.concat(Array(TransactionType.ReissueTransaction.id.toByte), tx.sender.publicKey, tx.issue.currency.b.id,
      Longs.toByteArray(tx.quantity),
      Array(booleanWithByte(tx.reissuable)), Longs.toByteArray(tx.fee), Longs.toByteArray(tx.timestamp)))
  }
}

object TransferTransactionSigner extends WavesSigner[TransferTransaction, Signature64]
  with ArraySignCreator[TransferTransaction] {
  override def sign(tx: TransferTransaction)(implicit bc: WavesBlockChain): Signed[TransferTransaction, Signature64] = {
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(tx.sender.privateKey.get,
      createSign(tx).value))
    SignedTransaction(tx, signature)
  }

  override def createSign(tx: TransferTransaction): ArraySign = {
    val timestampBytes = Longs.toByteArray(tx.timestamp)
    // todo as serializer
    val assetIdBytes = writeArrayOption(tx.transfer.currency.fold(_ => None, a => Some(a.id)))
    val amountBytes = Longs.toByteArray(tx.quantity)
    val feeAssetBytes = writeArrayOption(tx.feeMoney.currency.fold(_ => None, a => Some(a.id)))
    val feeBytes = Longs.toByteArray(tx.fee)

    ArraySign(Bytes.concat(Array(TransactionType.TransferTransaction.id.toByte), tx.sender.publicKey, assetIdBytes, feeAssetBytes,
      timestampBytes, amountBytes, feeBytes,
      tx.recipient.address, arrayWithSize(tx.attachment)))
  }
}

object TransactionSigner extends WavesSigner[WavesBlockChain#T, Signature64] {
  private def implicitlySign[TX <: WavesBlockChain#T](tx: TX)(implicit signer: WavesSigner[TX, Signature64]): Signed[TX, Signature64] = {
    signer.sign(tx)
  }

  override def sign(tx: WavesTransaction)(implicit bc: WavesBlockChain): Signed[WavesTransaction, Signature64] = {
    tx match {
      case tx: GenesisTransaction => implicitlySign(tx)
      case tx: PaymentTransaction => implicitlySign(tx)
      case tx: IssueTransaction => implicitlySign(tx)
      case tx: ReissueTransaction => implicitlySign(tx)
      case tx: TransferTransaction => implicitlySign(tx)
    }
  }
}
