package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{Signature32, Signature64}

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
  override val configuration: WavesConfiguration = new WavesConfiguration.Default("T".toByte)(this)
}

trait MainNetWavesBlockChainConfiguration {
  self: WavesBlockChain =>
  override val configuration: WavesConfiguration = new WavesConfiguration.Default("W".toByte)(this)
}
