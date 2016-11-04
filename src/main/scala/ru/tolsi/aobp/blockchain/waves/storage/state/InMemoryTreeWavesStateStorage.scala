package ru.tolsi.aobp.blockchain.waves.storage.state
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesStateChangeCalculator, WavesСurrency}
import ru.tolsi.aobp.blockchain.waves.storage.NotThreadSafeStorage
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage

import scala.collection.mutable
import scala.util.Try

trait InMemoryState extends WavesBlockChain {
  trait InMemoryState extends NotThreadSafeStorage {
    self: AbstractWavesStateStorage =>
    protected val state = new mutable.AnyRefMap[BA, BalanceValue]
    def currentState: Map[BA, BalanceValue] = state.toMap

    def currentBalance(balanceAccount: BA): Option[BalanceValue] = state.get(balanceAccount)

    def currentBalance(balanceAccount: Address): Map[WavesСurrency, BalanceValue] = currentState.filter(_._1._1 == balanceAccount).map(kv => kv._1._2 -> kv._2)

    def currentBalance(balanceAccount: Address, currency: WavesСurrency): Option[BalanceValue] = state.get((balanceAccount, currency))

  }

  class InMemoryTreeWavesStateStorage(blocksStorage: WavesBlockStorage) extends AbstractWavesStateStorage(blocksStorage) with InMemoryState with NotThreadSafeStorage {
    override def add(b: SignedBlock): Unit = {
      tryToApplyBlock(b, state).foreach(_ => blocksStorage.put(b))
    }

    override def switchTo(b: SignedBlock): Unit = {
      // todo try to rollback to new state
      ???
    }

    private def tryToApplyBlock(b: B, updatedState: mutable.AnyRefMap[BA, BalanceValue]): Try[Map[BA, BalanceValue]] = Try {
      WavesStateChangeCalculator.calculateStateChanges(b).foreach { bc =>
        val oldBalance = updatedState.getOrElseUpdate(bc.account, state.getOrElse(bc.account, 0L))
        val newBalance = Math.addExact(oldBalance, bc.amount)
        if (newBalance < 0) {
          throw new IllegalStateException("Negative balance")
        }
        updatedState.update(bc.account, newBalance)
      }
      updatedState.toMap
    }

    override def isLeadToValidState(b: SignedBlock): Boolean = {
      tryToApplyBlock(b, new mutable.AnyRefMap[BA, BalanceValue]()).isSuccess
    }
  }

}
