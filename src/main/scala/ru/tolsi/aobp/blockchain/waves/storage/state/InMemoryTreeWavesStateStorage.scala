package ru.tolsi.aobp.blockchain.waves.storage.state

import ru.tolsi.aobp.blockchain.waves.storage.NotThreadSafeStorage
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesStateChangeCalculator, WavesСurrency}

import scala.collection.mutable
import scala.util.Try

trait InMemoryState[W <: WavesBlockChain] extends NotThreadSafeStorage {
  self: AbstractWavesStateStorage[W] =>
  protected val state = new mutable.AnyRefMap[W#BA, BalanceValue]

  def currentState: Map[W#BA, BalanceValue] = state.toMap

  def currentBalance(balanceAccount: W#BA): Option[BalanceValue] = state.get(balanceAccount)

  def currentBalance(balanceAccount: W#AD): Map[WavesСurrency, BalanceValue] = currentState.filter(_._1._1 == balanceAccount).map(kv => kv._1._2 -> kv._2)

  def currentBalance(balanceAccount: W#AD, currency: WavesСurrency): Option[BalanceValue] = state.get((balanceAccount, currency))

}

class InMemoryTreeWavesStateStorage[W <: WavesBlockChain](blocksStorage: WavesBlockStorage[W]) extends AbstractWavesStateStorage[W](blocksStorage) with InMemoryState[W] with NotThreadSafeStorage {
  override def add(b: W#SB[W#B]): Unit = {
    tryToApplyBlock(b, state).foreach(_ => blocksStorage.put(b))
  }

  override def switchTo(b: W#SB[W#B]): Unit = {
    // todo try to rollback to new state
    ???
  }

  private val stateCalculator = new WavesStateChangeCalculator[W]

  private def tryToApplyBlock(b: W#B, updatedState: mutable.AnyRefMap[W#BA, BalanceValue]): Try[Map[W#BA, BalanceValue]] = Try {
    stateCalculator.calculateStateChanges(b).foreach { bc =>
      val oldBalance = updatedState.getOrElseUpdate(bc.account, state.getOrElse(bc.account, 0L))
      val newBalance = Math.addExact(oldBalance, bc.amount)
      if (newBalance < 0) {
        throw new IllegalStateException("Negative balance")
      }
      updatedState.update(bc.account, newBalance)
    }
    updatedState.toMap
  }

  override def isLeadToValidState(b: W#SB[W#B]): Boolean = {
    tryToApplyBlock(b, new mutable.AnyRefMap[W#BA, BalanceValue]()).isSuccess
  }
}
