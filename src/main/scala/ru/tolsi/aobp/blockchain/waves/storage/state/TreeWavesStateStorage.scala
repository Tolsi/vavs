package ru.tolsi.aobp.blockchain.waves.storage.state
import ru.tolsi.aobp.blockchain.waves.WavesСurrency

class TreeWavesStateStorage extends AbstractWavesStateStorage {
  override def currentState: Map[(String, WavesСurrency), BalanceValue] = ???

  override def currentBalance(balanceAccount: (String, WavesСurrency)): BalanceValue = ???

  override def currentBalance(balanceAccount: String): Map[WavesСurrency, BalanceValue] = ???

  override def currentBalance(balanceAccount: String, currency: WavesСurrency): BalanceValue = ???

  override def add(b: Block): Unit = ???

  override def switchTo(b: Block): Unit = ???
}
