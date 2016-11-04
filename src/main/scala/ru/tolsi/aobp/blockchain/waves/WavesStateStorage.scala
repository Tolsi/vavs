package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.StateStorage

private[waves] abstract class WavesStateStorage extends StateStorage[WavesBlockChain] {
  type AssetId = String
  type BalanceAccount = String
  override type Balance = Long

  def currentState: Map[BalanceAccount, Balance]

  def currentBalance(balanceAccount: BalanceAccount): Balance

  def currentAssetBalance(balanceAccount: BalanceAccount, asset: AssetId): Balance

  def apply(b: WavesBlockChain#B): Unit

  // todo tree storage
  def jumpToState(b: WavesBlockChain#B): Unit
}
