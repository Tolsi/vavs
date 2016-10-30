package ru.tolsi.aobp.blockchain.base

import rx.Observable

trait Signable

sealed trait Signature[V] {
  def value: V
}

class Signature32[V](val value: V) extends Signature[V]

class Signature64[V](val value: V) extends Signature[V]

sealed trait Validable

abstract class AbstractValidationError[+V <: Validable](m: => String) {
  def message: String = m
}

trait Signed[T <: Signable, V, S <: Signature[V]] {
  def signature: S
}

trait StateChangeReason

abstract class ValidatorOnBlockChain[BC <: BlockChain, V <: Validable, E <: AbstractValidationError[V]] {
  def validate(tx: V)(implicit blockChain: BC): Either[Seq[E], V]
}

trait BlockChain {
  protected abstract class BlockChainBlock extends Validable with Signable {
    type Id
  }

  protected abstract class BlockChainTransaction extends Validable with Signable with StateChangeReason {
    protected trait ValidationError extends AbstractValidationError[this.type]
  }

  type T <: BlockChainTransaction
  type B <: BlockChainBlock
  type AС <: BlockChainAccount
  type AВ <: BlockChainAddress

  protected abstract class BlockChainAccount(val publicKey: Array[Byte], val privateKey: Option[Array[Byte]])

  protected abstract class BlockChainAddress(val address: Array[Byte]) extends Validable

  protected trait BlockChainSignedTransaction[V] extends BlockChainTransaction with Signed[T, V, Signature[V]]

  protected trait BlockChainSignedBlock[V] extends BlockChainBlock with Signed[B, V, Signature[V]]


  abstract class TransactionValidationError[+TX <: T](message: => String) extends AbstractValidationError[TX](message)
  abstract class BlockValidationError[+BL <: B](message: => String) extends AbstractValidationError[BL](message)

  protected abstract class TransactionValidator[TX <: T] extends ValidatorOnBlockChain[this.type, TX, TransactionValidationError[TX]]
  protected abstract class BlockValidator[BL <: B] extends ValidatorOnBlockChain[this.type, BL, BlockValidationError[BL]]

  protected def genesis: B

  protected def txValidator: TransactionValidator[T]

  protected def blockValidator: BlockValidator[B]
}


abstract class Wallet[BC <: BlockChain] {
  def createNewAccount: BC#AС
}

abstract class BlockGenerator[BC <: BlockChain] {
  def blocks: Observable[BC#B]
}

trait BlockChainApp[BC <: BlockChain] {
  def blockChain: BlockChain

  def wallet: Wallet[BC]

  def utx: BC#T

  def miner: BlockGenerator[BC]
}

// todo хранить блокчейн как дерево и удалять неосновные ветки после N
abstract class BlockStorage[BC <: BlockChain] {
  def put(block: BC#B): Unit

  def get(id: BC#B#Id): Option[BC#B]

  def contains(id: BC#B#Id): Boolean
}

abstract class UnconfirmedTransactionStorage[BC <: BlockChain] {
  def put(tx: BC#T): Unit

  def all: Seq[BC#T]

  def remove(tx: BC#T): Option[BC#T]
}

abstract class StateStorage[BC <: BlockChain] {
  type BalanceAccount
  type Balance = Long

  def currentState: Map[BalanceAccount, Balance]

  def currentBalance(balanceAccount: BalanceAccount): Balance

  def apply(b: BC#B): Unit

  def rollback(b: BC#B): Unit
}

sealed trait ProtocolRequest

trait IncomingNetworkLayer[BC <: BlockChain] {
  def incomingRequests: Observable[ProtocolRequest]

  def incomingTx: Observable[BC#T]

  def incomingBlocks: Observable[BC#B]
}

trait OutgoingNetworkLayer[BC <: BlockChain] {
  def outgoingRequests: Observable[ProtocolRequest]

  def outgoingTx: Observable[BC#T]

  def outgoingBlocks: Observable[BC#B]
}

trait NetworkLayer[BC <: BlockChain] extends IncomingNetworkLayer[BC] with OutgoingNetworkLayer[BC]
