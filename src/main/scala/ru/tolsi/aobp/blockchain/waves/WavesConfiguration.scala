package ru.tolsi.aobp.blockchain.waves

import scala.concurrent.duration._

trait WavesConfiguration {
  this: WavesBlockChain =>

  def configuration: Configuration

  trait Configuration {
    def chainId: Byte

    def maxTimeDriftMillis: Long
    def maxTimeForUnconfirmedMillis: Long
    def maxTxAndBlockDiffMillis: Long
    def maxTransactionsPerBlock: Int
  }
  abstract class DefaultConfiguration extends Configuration {
    override val maxTimeDriftMillis = 15.seconds.toMillis
    override val maxTimeForUnconfirmedMillis = 90.minutes.toMillis
    override val maxTxAndBlockDiffMillis = 2.hour.toMillis
    override val maxTransactionsPerBlock = 100
  }
}
