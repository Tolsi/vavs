package ru.tolsi.aobp.blockchain.waves.storage.state

import ru.tolsi.aobp.blockchain.waves.block.{SignedBlock, WavesBlock}
import ru.tolsi.aobp.blockchain.waves.state.WavesStateChangeCalculator
import ru.tolsi.aobp.blockchain.waves.storage.NotThreadSafeStorage
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction
import ru.tolsi.aobp.blockchain.waves._

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

trait InMemoryState extends NotThreadSafeStorage {
  self: AbstractWavesStateStorage =>
  protected val state = new mutable.AnyRefMap[BalanceAccount, BalanceValue]

  def currentState: Map[BalanceAccount, BalanceValue] = state.toMap

  def currentBalance(balanceAccount: BalanceAccount): Option[BalanceValue] = state.get(balanceAccount)

  def currentBalance(address: Address): Map[WavesСurrency, BalanceValue] = currentState.filter(_._1.address == address).map(kv => kv._1.currency -> kv._2)

  def currentBalance(balanceAccount: Address, currency: WavesСurrency): Option[BalanceValue] = state.get(BalanceAccount(balanceAccount, currency))

}

class InMemoryTreeWavesStateStorage(blocksStorage: WavesBlockStorage) extends AbstractWavesStateStorage(blocksStorage) with InMemoryState with NotThreadSafeStorage {
  override def add(b: SB[WavesBlock]): Unit = {
    tryToApplyBlock(b, state).foreach(_ => blocksStorage.put(b))
  }

  override def switchTo(b: SB[WavesBlock]): Unit = {
    // todo try to rollback to new state
    ???
  }

  private val stateCalculator = new WavesStateChangeCalculator

  private def tryToApplyBlock(b: WavesBlock, updatedState: mutable.AnyRefMap[BalanceAccount, BalanceValue]): Try[Map[BalanceAccount, BalanceValue]] = Try {
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

  private def tryToApplyBlock(txs: Seq[WavesTransaction], updatedState: mutable.AnyRefMap[BalanceAccount, BalanceValue]): Try[mutable.AnyRefMap[BalanceAccount, BalanceValue]] = txs.foldLeft[Try[mutable.AnyRefMap[BalanceAccount, BalanceValue]]](Success(updatedState)) {
    case (m, t) => m match {
      case Success(m) => tryToApplyBlock(t, m)
      case f@Failure(_) => f
    }
  }

  private def tryToApplyBlock(t: WavesTransaction, updatedState: mutable.AnyRefMap[BalanceAccount, BalanceValue]): Try[mutable.AnyRefMap[BalanceAccount, BalanceValue]] = Try {
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

  override def isLeadToValidState(b: WavesBlock): Boolean = {
    tryToApplyBlock(b, new mutable.AnyRefMap[BalanceAccount, BalanceValue]()).isSuccess
  }

  override def isLeadToValidState(t: WavesTransaction): Boolean = {
    tryToApplyBlock(t, new mutable.AnyRefMap[BalanceAccount, BalanceValue]()).isSuccess
  }

  override def isLeadToValidState(t: Seq[WavesTransaction]): Boolean = {
    tryToApplyBlock(t, new mutable.AnyRefMap[BalanceAccount, BalanceValue]()).isSuccess
  }

  override def lastBlock: SignedBlock[WavesBlock] = ???
}
