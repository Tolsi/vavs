package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves._

case class IssueTransaction(sender: Account,
                            name: Array[Byte],
                            description: Array[Byte],
                            issue: WavesMoney[Right[Waves.type, Asset]],
                            decimals: Byte,
                            reissuable: Boolean,
                            feeMoney: WavesMoney[Left[Waves.type, Asset]],
                            timestamp: Long) extends AssetIssuanceTransaction {
  override def typeId = TransactionType.IssueTransaction

  override val recipient: Address = sender.address

  override def amount: BigDecimal = issue.amount

  override def currency: WavesСurrency = issue.currency.b

  override def feeCurrency: WavesСurrency = feeMoney.currency.a

  override def fee: Long = feeMoney.value

  override def quantity: Long = issue.value
}
