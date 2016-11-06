package ru.tolsi.aobp.blockchain.waves.transaction.signer

import com.google.common.primitives.{Bytes, Longs}
import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base.{ArraySign, ArraySignCreator, Signature64, Signed}
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesSigner}
import ru.tolsi.aobp.blockchain.waves.transaction.{SignedTransaction, TransactionType, TransferTransaction}

private[signer] class TransferTransactionSigner extends WavesSigner[TransferTransaction, SignedTransaction[TransferTransaction], Signature64]
  with ArraySignCreator[TransferTransaction] {
  override def sign(tx: TransferTransaction)(implicit bc: WavesBlockChain): SignedTransaction[TransferTransaction] = {
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
