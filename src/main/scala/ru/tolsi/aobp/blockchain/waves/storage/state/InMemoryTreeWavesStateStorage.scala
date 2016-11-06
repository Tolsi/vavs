package ru.tolsi.aobp.blockchain.waves.storage.state

import ru.tolsi.aobp.blockchain.waves.state.WavesStateChangeCalculator
import ru.tolsi.aobp.blockchain.waves.storage.NotThreadSafeStorage
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesСurrency}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

trait InMemoryState extends NotThreadSafeStorage {
  self: AbstractWavesStateStorage =>
  protected val state = new mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]

  def currentState: Map[WavesBlockChain#BA, BalanceValue] = state.toMap

  def currentBalance(balanceAccount: WavesBlockChain#BA): Option[BalanceValue] = state.get(balanceAccount)

  def currentBalance(balanceAccount: WavesBlockChain#AD): Map[WavesСurrency, BalanceValue] = currentState.filter(_._1._1 == balanceAccount).map(kv => kv._1._2 -> kv._2)

  def currentBalance(balanceAccount: WavesBlockChain#AD, currency: WavesСurrency): Option[BalanceValue] = state.get((balanceAccount, currency))

}

class InMemoryTreeWavesStateStorage(blocksStorage: WavesBlockStorage) extends AbstractWavesStateStorage(blocksStorage) with InMemoryState with NotThreadSafeStorage {
  override def add(b: WavesBlockChain#SB[WavesBlockChain#B]): Unit = {
    tryToApplyBlock(b, state).foreach(_ => blocksStorage.put(b))
  }

  override def switchTo(b: WavesBlockChain#SB[WavesBlockChain#B]): Unit = {
    // todo try to rollback to new state
    ???
  }

  private val stateCalculator = new WavesStateChangeCalculator

  private def tryToApplyBlock(b: WavesBlockChain#B, updatedState: mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]): Try[Map[WavesBlockChain#BA, BalanceValue]] = Try {
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

  private def tryToApplyBlock(txs: Seq[WavesBlockChain#T], updatedState: mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]): Try[mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]] = txs.foldLeft[Try[mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]]](Success(updatedState)) {
    case (m, t) => m match {
      case Success(m) => tryToApplyBlock(t, m)
      case f@Failure(_) => f
    }
  }

  private def tryToApplyBlock(t: WavesBlockChain#T, updatedState: mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]): Try[mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]] = Try {
    stateCalculator.calculateStateChanges(t).foreach { bc =>
      val oldBalance = updatedState.getOrElseUpdate(bc.account, state.getOrElse(bc.account, 0L))
      val newBalance = Math.addExact(oldBalance, bc.amount)
      if (newBalance < 0) {
        throw new IllegalStateException("Negative balance")
      }
      updatedState.update(bc.account, newBalance)
    }
    updatedState
  }

  override def isLeadToValidState(b: WavesBlockChain#B): Boolean = {
    tryToApplyBlock(b, new mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]()).isSuccess
  }

  override def isLeadToValidState(t: WavesTransaction): Boolean = {
    tryToApplyBlock(t, new mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]()).isSuccess
  }

  override def isLeadToValidState(t: Seq[WavesTransaction]): Boolean = {
    tryToApplyBlock(t, new mutable.AnyRefMap[WavesBlockChain#BA, BalanceValue]()).isSuccess
  }
}
