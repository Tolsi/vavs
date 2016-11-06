package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves._

case class TransferTransaction(timestamp: Long,
                               sender: WavesBlockChain#AC,
                               recipient: Address,
                               transfer: WavesMoney[Either[Waves.type, Asset]],
                               feeMoney: WavesMoney[Either[Waves.type, Asset]],
                               attachment: Array[Byte]) extends WavesTransaction {
  override def typeId = TransactionType.TransferTransaction

  override def amount: BigDecimal = transfer.amount

  override def currency: WavesСurrency = transfer.currency.fold(identity, identity)

  override def feeCurrency: WavesСurrency = feeMoney.currency.fold(identity, identity)

  override def fee: Long = feeMoney.value

  override def quantity: Long = transfer.value
}
