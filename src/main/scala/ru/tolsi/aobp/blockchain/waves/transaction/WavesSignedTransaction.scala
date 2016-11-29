package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves._

case class WavesSignedTransaction[+TX <: WavesTransaction](tx: TX, signature: Signature64) extends WavesTransaction with Signed[TX, Signature64] {
  val signed: TX = tx

  override val typeId: TransactionType.Value = tx.typeId

  override val recipient: Address = tx.recipient

  override val timestamp: Long = tx.timestamp

  override val amount: BigDecimal = tx.amount

  override val quantity: Long = tx.quantity

  override val currency: WavesСurrency = tx.currency

  override val fee: Long = tx.fee

  override val feeCurrency: WavesСurrency = tx.feeCurrency
}
