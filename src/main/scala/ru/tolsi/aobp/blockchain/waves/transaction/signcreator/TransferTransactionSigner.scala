package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import com.google.common.primitives.{Bytes, Longs}
import ru.tolsi.aobp.blockchain.base.SignCreator
import ru.tolsi.aobp.blockchain.waves.{Sign, SignCreator}
import ru.tolsi.aobp.blockchain.waves.serializer.BytesUtils._
import ru.tolsi.aobp.blockchain.waves.transaction.{TransactionType, TransferTransaction}

private[signcreator] class TransferTransactionSignCreator extends SignCreator[TransferTransaction] {
  override def createSign(tx: TransferTransaction): Sign[TransferTransaction] = {
    val timestampBytes = Longs.toByteArray(tx.timestamp)
    // todo as serializer?
    val assetIdBytes = optionByteArrayToByteArray(tx.transfer.currency.fold(_ => None, a => Some(a.id)))
    val amountBytes = Longs.toByteArray(tx.quantity)
    val feeAssetBytes = optionByteArrayToByteArray(tx.feeMoney.currency.fold(_ => None, a => Some(a.id)))
    val feeBytes = Longs.toByteArray(tx.fee)

    Sign(Bytes.concat(Array(TransactionType.TransferTransaction.id.toByte), tx.sender.publicKey, assetIdBytes, feeAssetBytes,
      timestampBytes, amountBytes, feeBytes,
      tx.recipient.address, arrayWithSize(tx.attachment)))
  }
}
