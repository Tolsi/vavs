package ru.tolsi.aobp.blockchain.waves

private[waves] trait WavesTransactions {
  this: WavesBlockChain =>

  object TransactionType extends Enumeration {
    val GenesisTransaction = Value(1)
    val PaymentTransaction = Value(2)
    val IssueTransaction = Value(3)
    val TransferTransaction = Value(4)
    val ReissueTransaction = Value(5)
  }

  abstract class Transaction extends BlockChainTransaction {
    def typeId: TransactionType.Value

    val recipient: Address

    def timestamp: Long

    def amount: Long

    def currency: WavesСurrency

    def fee: Long

    def feeCurrency: WavesСurrency

    // todo is it good idea? external implicit balance changes calculator
    //  def balanceChanges(): Seq[(WavesAccount, Long)]
  }

  abstract class SignedTransaction[TX <: T](tx: TX) extends Transaction with BlockChainSignedTransaction[TX, Array[Byte], ArraySignature64] {
    override def signed: TX = tx

    override def typeId: TransactionType.Value = tx.typeId

    override val recipient: Address = tx.recipient

    override def timestamp: Long = tx.timestamp

    override def amount: Long = tx.amount

    override def currency: WavesСurrency = tx.currency

    override def fee: Long = tx.fee

    override def feeCurrency: WavesСurrency = tx.feeCurrency
  }

  case class GenesisTransaction(recipient: Address, timestamp: Long, amount: Long) extends Transaction {
    override val typeId = TransactionType.GenesisTransaction

    override val fee: Long = 0

    override val currency: WavesСurrency = Waves

    override val feeCurrency: WavesСurrency = Waves
  }

  case class PaymentTransaction(sender: Account,
                                override val recipient: Address,
                                override val amount: Long,
                                override val fee: Long,
                                override val timestamp: Long) extends Transaction {
    override def typeId = TransactionType.PaymentTransaction

    override def currency: WavesСurrency = Waves

    override def feeCurrency: WavesСurrency = Waves
  }

  sealed trait AssetIssuanceTransaction extends Transaction {
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

    override def amount: Long = issue.value

    override def currency: WavesСurrency = issue.currency.b

    override def feeCurrency: WavesСurrency = feeMoney.currency.a

    override def fee: Long = feeMoney.value
  }

  case class ReissueTransaction(sender: Account,
                                issue: WavesMoney[Right[Waves.type, Asset]],
                                reissuable: Boolean,
                                feeMoney: WavesMoney[Left[Waves.type, Asset]],
                                timestamp: Long) extends AssetIssuanceTransaction {
    override def typeId = TransactionType.ReissueTransaction

    override val recipient: Address = sender.address

    override def amount: Long = issue.value

    override def currency: WavesСurrency = issue.currency.b

    override def feeCurrency: WavesСurrency = feeMoney.currency.a

    override def fee: Long = feeMoney.value
  }

  case class TransferTransaction(timestamp: Long,
                                 sender: Account,
                                 recipient: Address,
                                 transfer: WavesMoney[Either[Waves.type, Asset]],
                                 feeMoney: WavesMoney[Either[Waves.type, Asset]],
                                 attachment: Array[Byte]) extends Transaction {
    override def typeId = TransactionType.TransferTransaction

    override def amount: Long = transfer.value

    override def currency: WavesСurrency = transfer.currency.fold(identity, identity)

    override def feeCurrency: WavesСurrency = feeMoney.currency.fold(identity, identity)

    override def fee: Long = feeMoney.value
  }

}
