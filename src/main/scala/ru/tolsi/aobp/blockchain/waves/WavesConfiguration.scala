package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.waves.block.{GenesisBlock, WavesBlock}
import ru.tolsi.aobp.blockchain.waves.transaction.GenesisTransaction

import scala.concurrent.duration._

object WavesConfiguration {

  class Default(val chainId: Byte)(implicit wbc: WavesBlockChain) extends WavesConfiguration {
    override val initialBaseTarget = 153722867L
    override val totalWaves = 100000000L
    override val unitsInWave = 100000000L
    override val initialBalance = totalWaves * unitsInWave

    override val maxTimeDriftMillis = 15.seconds.toMillis
    override val maxTimeForUnconfirmedMillis = 90.minutes.toMillis
    override val maxTxAndBlockDiffMillis = 2.hour.toMillis
    override val maxTransactionsPerBlock = 100

    override val genesisGenerationSignature = new Signature32(Array.fill(32)(0.toByte))
    override val genesisGeneratorAccount: Account = Account(Array.fill(32)(0.toByte))
    override val genesisSignature: Signature64 = new Signature64(Array.fill(64)(0.toByte))
  }

}

abstract class WavesConfiguration {
  def chainId: Byte

  def initialBaseTarget: Long

  def initialBalance: Long

  def genesisGenerationSignature: Signature32

  def genesisGeneratorAccount: Account

  def genesisSignature: Signature64

  def totalWaves: Long

  def unitsInWave: Long

  def maxTimeDriftMillis: Long

  def maxTimeForUnconfirmedMillis: Long

  def maxTxAndBlockDiffMillis: Long

  def maxTransactionsPerBlock: Int
}

trait TestNetWavesBlockChainConfiguration {
  self: WavesBlockChain =>
  implicit val bc = self
  override val configuration: WavesConfiguration = new WavesConfiguration.Default("T".toByte)(this)
  val genesisTimestamp = 1460952000000L
  val genesisTransactions = {
    val singleNodeBalance = (configuration.initialBalance * 0.02).toLong
    val transactions = Seq(
      GenesisTransaction(Address("3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8"), genesisTimestamp, 2 * singleNodeBalance).signed,
      GenesisTransaction(Address("3NBVqYXrapgJP9atQccdBPAgJPwHDKkh6A8"), genesisTimestamp, singleNodeBalance).signed,
      GenesisTransaction(Address("3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh"), genesisTimestamp, singleNodeBalance).signed,
      GenesisTransaction(Address("3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd"), genesisTimestamp, singleNodeBalance).signed,
      GenesisTransaction(Address("3N18z4B8kyyQ96PhN5eyhCAbg4j49CgwZJx"), genesisTimestamp, configuration.initialBalance - 5 * singleNodeBalance).signed
    )
    require(transactions.foldLeft(0L)(_ + _.signed.quantity) == configuration.initialBalance)
    transactions
  }
  override val genesis: WavesBlock = GenesisBlock(genesisTimestamp, new Signature64(Array.fill[Byte](64)(0)), configuration.initialBaseTarget, configuration.genesisGeneratorAccount, configuration.genesisGenerationSignature, WavesMoney[Either[Waves.type, Asset]](0, Left(Waves)), genesisTransactions)

}

trait MainNetWavesBlockChainConfiguration {
  self: WavesBlockChain =>
  override val configuration: WavesConfiguration = new WavesConfiguration.Default("W".toByte)(this)
}
