package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves.{Address, Waves, WavesBlockChain, WavesСurrency}

case class PaymentTransaction(sender: WavesBlockChain#AC,
                              override val recipient: Address,
                              override val quantity: Long,
                              override val fee: Long,
                              override val timestamp: Long) extends WavesTransaction {
  override def typeId = TransactionType.PaymentTransaction

  override def currency: WavesСurrency = Waves

  override def feeCurrency: WavesСurrency = Waves

  override def amount: BigDecimal = BigDecimal(quantity)
}
