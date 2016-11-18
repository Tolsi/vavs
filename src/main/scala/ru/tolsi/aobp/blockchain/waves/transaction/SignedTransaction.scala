package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.base.BlockChainSignedTransaction
import ru.tolsi.aobp.blockchain.waves._

case class SignedTransaction[TX <: WavesBlockChain#T](tx: TX, signature: Signature64) extends WavesTransaction with BlockChainSignedTransaction[WavesBlockChain, TX, Signature64] {
  override def signed: TX = tx

  override def typeId: TransactionType.Value = tx.typeId

  override val recipient: Address = tx.recipient

  override def timestamp: Long = tx.timestamp

  override def amount: BigDecimal = tx.amount

  override def quantity: Long = tx.quantity

  override def currency: WavesСurrency = tx.currency

  override def fee: Long = tx.fee

  override def feeCurrency: WavesСurrency = tx.feeCurrency
}
