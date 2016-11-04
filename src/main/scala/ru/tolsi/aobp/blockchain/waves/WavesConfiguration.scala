package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{Signature32, Signature64}

import scala.concurrent.duration._

trait WavesConfiguration {
  this: WavesBlockChain =>

  def configuration: Configuration

  trait Configuration {
    def chainId: Byte
    def initialBaseTarget: Long

    def genesisGenerationSignature: Signature32
    def genesisGeneratorAccount: Account
    def genesisSignature: Signature64

    def maxTimeDriftMillis: Long
    def maxTimeForUnconfirmedMillis: Long
    def maxTxAndBlockDiffMillis: Long
    def maxTransactionsPerBlock: Int
  }

  abstract class DefaultConfiguration extends Configuration {
    override val initialBaseTarget = 153722867L

    override val maxTimeDriftMillis = 15.seconds.toMillis
    override val maxTimeForUnconfirmedMillis = 90.minutes.toMillis
    override val maxTxAndBlockDiffMillis = 2.hour.toMillis
    override val maxTransactionsPerBlock = 100

    override val genesisGenerationSignature = Array.fill(32)(0: Byte)
    override val genesisGeneratorAccount: Account = Account(Array.fill(32)(0.toByte))
    override val genesisSignature: Signature64 = new Signature64(Array.fill(64)(0.toByte))
  }
}
