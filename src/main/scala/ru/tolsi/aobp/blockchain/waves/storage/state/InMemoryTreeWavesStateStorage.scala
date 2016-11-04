package ru.tolsi.aobp.blockchain.waves.storage.state
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesСurrency}
import ru.tolsi.aobp.blockchain.waves.storage.NotThreadSafeStorage
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage

import scala.collection.mutable

trait InMemoryState extends NotThreadSafeStorage {
  self: AbstractWavesStateStorage =>
  private val state = new mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]
  override def currentState: Map[WavesBlockChain#BA, BalanceValue] = state.toMap

  override def currentBalance(balanceAccount: WavesBlockChain#BA): Option[BalanceValue] = state.get(balanceAccount)

  override def currentBalance(balanceAccount: WavesBlockChain#Address): Map[WavesСurrency, BalanceValue] = currentState.filter(_._1._1 == balanceAccount).map(kv => kv._1._2 -> kv._2)

  override def currentBalance(balanceAccount: WavesBlockChain#Address, currency: WavesСurrency): Option[BalanceValue] = state.get((balanceAccount, currency))

}

class InMemoryTreeWavesStateStorage(blocksStorage: WavesBlockStorage) extends AbstractWavesStateStorage(blocksStorage) with InMemoryState with NotThreadSafeStorage {
  override def add(b: SignedBlock): Unit = {
    blocksStorage.put(b)
    b.transactions
    // todo apply state
  }

  override def switchTo(b: SignedBlock): Unit = {
    // todo try to rollback to new state
    ???
  }

  override def isValid(stateChanges: Seq[WavesBlockChain#StateChange]): Boolean = {
    // todo try to apply state
    ???
  }
}
