package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.transaction.{IssueTransaction, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves.transaction.validator.error.{WrongAssetDecimals, WrongAssetDescription, WrongAssetName, WrongFee}

import scala.util.{Left, Right}

private[validator] class IssueTransactionValidator extends TransactionValidator[IssueTransaction] {
  val MaxDescriptionLength = 1000
  val MaxAssetNameLength = 16
  val MinAssetNameLength = 4
  val MinFee = 100000000
  val MaxDecimals = 8

  private[waves] def smallFeeValidation(fee: Long): Option[WrongFee] = {
    if (fee < MinFee) {
      Some(new WrongFee(s"$fee < $MinFee"))
    } else None
  }

  private[waves] def maxAssetNameLength(assetName: Array[Byte]): Option[WrongAssetName] = {
    if (assetName.length > MaxAssetNameLength) {
      Some(new WrongAssetName(s"${assetName.length} > $MaxAssetNameLength"))
    } else None
  }

  private[waves] def minAssetNameLength(assetName: Array[Byte]): Option[WrongAssetName] = {
    if (assetName.length < MinAssetNameLength) {
      Some(new WrongAssetName(s"${assetName.length} < $MinAssetNameLength"))
    } else None
  }

  private[waves] def maxDescriptorNameLength(description: Array[Byte]): Option[WrongAssetDescription] = {
    if (description.length > MaxDescriptionLength) {
      Some(new WrongAssetDescription(s"${description.length} > $MaxDescriptionLength"))
    } else None
  }

  private[waves] def negativeDecimals(decimals: Int): Option[WrongAssetDecimals] = {
    if (decimals < 0) {
      Some(new WrongAssetDecimals(s"$decimals < 0"))
    } else None
  }

  private[waves] def maxDecimals(decimals: Int): Option[WrongAssetDecimals] = {
    if (decimals > MaxDecimals) {
      Some(new WrongAssetDecimals(s"$decimals > $MaxDecimals"))
    } else None
  }

  override def validate(tx: IssueTransaction)(implicit bc: WavesBlockChain): ResultT = ???
}
