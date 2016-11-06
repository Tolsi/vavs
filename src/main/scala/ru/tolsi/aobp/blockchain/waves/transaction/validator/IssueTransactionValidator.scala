package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction.IssueTransaction
import ru.tolsi.aobp.blockchain.waves.transaction.validator.error.{WrongAssetDecimals, WrongAssetDescription, WrongAssetName, WrongFee}

import scala.util.{Left, Right}

private[validator] class IssueTransactionValidator extends AbstractTransactionValidator[IssueTransaction] {
  val MaxDescriptionLength = 1000
  val MaxAssetNameLength = 16
  val MinAssetNameLength = 4
  val MinFee = 100000000
  val MaxDecimals = 8

  private[waves] def smallFeeValidation(fee: Long): Option[WrongFee[IssueTransaction]] = {
    if (fee < MinFee) {
      Some(new WrongFee(s"$fee < $MinFee"))
    } else None
  }

  private[waves] def maxAssetNameLength(assetName: Array[Byte]): Option[WrongAssetName[IssueTransaction]] = {
    if (assetName.length > MaxAssetNameLength) {
      Some(new WrongAssetName(s"${assetName.length} > $MaxAssetNameLength"))
    } else None
  }

  private[waves] def minAssetNameLength(assetName: Array[Byte]): Option[WrongAssetName[IssueTransaction]] = {
    if (assetName.length < MinAssetNameLength) {
      Some(new WrongAssetName(s"${assetName.length} < $MinAssetNameLength"))
    } else None
  }

  private[waves] def maxDescriptorNameLength(description: Array[Byte]): Option[WrongAssetDescription[IssueTransaction]] = {
    if (description.length > MaxDescriptionLength) {
      Some(new WrongAssetDescription(s"${description.length} > $MaxDescriptionLength"))
    } else None
  }

  private[waves] def negativeDecimals(decimals: Int): Option[WrongAssetDecimals[IssueTransaction]] = {
    if (decimals < 0) {
      Some(new WrongAssetDecimals(s"$decimals < 0"))
    } else None
  }

  private[waves] def maxDecimals(decimals: Int): Option[WrongAssetDecimals[IssueTransaction]] = {
    if (decimals > MaxDecimals) {
      Some(new WrongAssetDecimals(s"$decimals > $MaxDecimals"))
    } else None
  }

  override def validate(tx: IssueTransaction)(implicit wbc: WavesBlockChain): Either[Seq[TransactionValidationError[WavesBlockChain, IssueTransaction]], IssueTransaction] = {
    val errors = Seq(
      addressValidation(tx.recipient),
      smallFeeValidation(tx.fee),
      maxAssetNameLength(tx.name),
      minAssetNameLength(tx.name),
      maxDescriptorNameLength(tx.description),
      negativeDecimals(tx.decimals),
      maxDecimals(tx.decimals),
      negativeAmountValidation(tx.quantity),
      overflowValidation(tx.issue, tx.feeMoney)
    ).flatten
    if (errors.nonEmpty) Left(errors) else Right(tx)
  }
}
