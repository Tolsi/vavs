package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.Signature64

private[waves] trait WavesTransactions {
  this: WavesBlockChain =>

  object TransactionType extends Enumeration {
    val GenesisTransaction = Value(1)
    val PaymentTransaction = Value(2)
    val IssueTransaction = Value(3)
    val TransferTransaction = Value(4)
    val ReissueTransaction = Value(5)
  }

  abstract sealed class WavesTransaction extends BlockChainTransaction {
    def typeId: TransactionType.Value

    val recipient: Address

    def timestamp: Long

    def amount: BigDecimal

    def quantity: Long

    def currency: WavesСurrency

    def fee: Long

    def feeCurrency: WavesСurrency

    // todo is it good idea? external implicit balance changes calculator
    //  def balanceChanges(): Seq[(WavesAccount, Long)]
  }

  case class SignedTransaction[TX <: T](tx: TX, signature: Signature64) extends WavesTransaction with BlockChainSignedTransaction[TX, Signature64] {
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

  case class GenesisTransaction(recipient: Address, timestamp: Long, quantity: Long) extends WavesTransaction {
    override val typeId = TransactionType.GenesisTransaction

    override val fee: Long = 0

    override val currency: WavesСurrency = Waves

    override val feeCurrency: WavesСurrency = Waves

    override def amount: BigDecimal = BigDecimal(quantity)
  }

  case class PaymentTransaction(sender: Account,
                                override val recipient: Address,
                                override val quantity: Long,
                                override val fee: Long,
                                override val timestamp: Long) extends WavesTransaction {
    override def typeId = TransactionType.PaymentTransaction

    override def currency: WavesСurrency = Waves

    override def feeCurrency: WavesСurrency = Waves

    override def amount: BigDecimal = BigDecimal(quantity)
  }

  sealed trait AssetIssuanceTransaction extends WavesTransaction {
    def issue: WavesMoney[Right[Waves.type, Asset]]

    def reissuable: Boolean
  }

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

  case class ReissueTransaction(sender: Account,
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

  case class TransferTransaction(timestamp: Long,
                                 sender: Account,
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

}
