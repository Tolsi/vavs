package ru.tolsi.aobp.blockchain.waves.storage.state

import ru.tolsi.aobp.blockchain.base.StateStorage
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesСurrency}

private[waves] abstract class AbstractWavesStateStorage extends StateStorage[WavesBlockChain] {
  override type BalanceAccount = (String, WavesСurrency)
  override type BalanceValue = Long

  def currentState: Map[BalanceAccount, BalanceValue]

  def currentBalance(balanceAccount: BalanceAccount): BalanceValue
  def currentBalance(balanceAccount: String): Map[WavesСurrency, BalanceValue]
  def currentBalance(balanceAccount: String, currency: WavesСurrency): BalanceValue

  def add(b: Block): Unit

  def switchTo(b: Block): Unit
}
