package ru.tolsi.aobp.blockchain.waves.storage.state

import ru.tolsi.aobp.blockchain.base.StateStorage
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesСurrency}

private[waves] abstract class AbstractWavesStateStorage[W <: WavesBlockChain](blocksStorage: WavesBlockStorage[W]) extends StateStorage[W, W#SB[W#B], W#BA] {
  override type BalanceValue = Long

  def currentState: Map[W#BA, BalanceValue]

  def currentBalance(balanceAccount: W#BA): Option[BalanceValue]
  def currentBalance(balanceAccount: W#Address): Map[WavesСurrency, BalanceValue]
  def currentBalance(balanceAccount: W#Address, currency: WavesСurrency): Option[BalanceValue]

  def add(b: W#SB[W#B]): Unit

  def switchTo(b: W#SB[W#B]): Unit
}
