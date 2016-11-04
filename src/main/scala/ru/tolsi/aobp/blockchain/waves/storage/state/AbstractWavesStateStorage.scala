package ru.tolsi.aobp.blockchain.waves.storage.state

import ru.tolsi.aobp.blockchain.base.StateStorage
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesСurrency}

private[waves] abstract class AbstractWavesStateStorage(blocksStorage: WavesBlockStorage) extends StateStorage[WavesBlockChain] {
  override type BalanceValue = Long

  def currentState: Map[WavesBlockChain#BA, BalanceValue]

  def currentBalance(balanceAccount: WavesBlockChain#BA): Option[BalanceValue]
  def currentBalance(balanceAccount: WavesBlockChain#Address): Map[WavesСurrency, BalanceValue]
  def currentBalance(balanceAccount: WavesBlockChain#Address, currency: WavesСurrency): Option[BalanceValue]

  def add(b: SignedBlock): Unit

  def switchTo(b: SignedBlock): Unit
}
