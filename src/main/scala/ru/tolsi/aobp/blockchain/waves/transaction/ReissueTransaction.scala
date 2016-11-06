package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves._

case class ReissueTransaction(sender: WavesBlockChain#AC,
                              issue: WavesMoney[Right[Waves.type, Asset]],
                              reissuable: Boolean,
                              feeMoney: WavesMoney[Left[Waves.type, Asset]],
                              timestamp: Long) extends AssetIssuanceTransaction {
  override def typeId = TransactionType.ReissueTransaction

  override val recipient: Address = sender.address

  override def amount: BigDecimal = issue.amount

  override def currency: WavesСurrency = issue.currency.b

  override def feeCurrency: WavesСurrency = feeMoney.currency.a

  override def fee: Long = feeMoney.value

  override def quantity: Long = issue.value
}
