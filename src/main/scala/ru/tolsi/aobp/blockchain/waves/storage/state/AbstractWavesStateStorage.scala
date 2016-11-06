package ru.tolsi.aobp.blockchain.waves.storage.state

import ru.tolsi.aobp.blockchain.base.StateStorage
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesСurrency}

private[waves] abstract class AbstractWavesStateStorage(blocksStorage: WavesBlockStorage) extends StateStorage[WavesBlockChain, WavesBlockChain#SB[WavesBlockChain#B], WavesBlockChain#BA] {
  override type BalanceValue = Long

  def currentState: Map[WavesBlockChain#BA, BalanceValue]

  def currentBalance(balanceAccount: WavesBlockChain#BA): Option[BalanceValue]

  def currentBalance(balanceAccount: WavesBlockChain#AD): Map[WavesСurrency, BalanceValue]

  def currentBalance(balanceAccount: WavesBlockChain#AD, currency: WavesСurrency): Option[BalanceValue]

  def add(b: WavesBlockChain#SB[WavesBlockChain#B]): Unit

  def switchTo(b: WavesBlockChain#SB[WavesBlockChain#B]): Unit
}
