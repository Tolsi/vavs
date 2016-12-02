package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves._

case class GenesisTransaction(recipient: Address, timestamp: Long, quantity: Long) extends WavesTransaction {
  override val typeId = TransactionType.GenesisTransaction

  override val fee: Long = 0

  override val currency: WavesСurrency = Waves

  override val feeCurrency: WavesСurrency = Waves

  override def amount: BigDecimal = BigDecimal(quantity)

  override def sender: Account = ???
}
