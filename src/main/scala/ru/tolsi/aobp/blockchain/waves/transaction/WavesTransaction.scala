package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializable
import ru.tolsi.aobp.blockchain.waves._

abstract class WavesTransaction extends Signable with Validable with StateChangeReason with BytesSerializable {
  def typeId: TransactionType.Value

  def recipient: Address

  def sender: Account

  def timestamp: Long

  def amount: BigDecimal

  def quantity: Long

  def currency: WavesСurrency

  def fee: Long

  def feeCurrency: WavesСurrency

  // todo is it good idea? external implicit balance changes calculator
  //  def balanceChanges(): Seq[(WavesAccount, Long)]
}
