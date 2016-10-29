package ru.tolsi.aobp.blockchain.base

import rx.Observable

trait Signable

sealed trait Signature[V] {
  def value: V
}

class Signature32[V](val value: V) extends Signature[V]

class Signature64[V](val value: V) extends Signature[V]

sealed trait Validable

sealed trait ValidationError[V <: Validable]

trait Signed[T <: Signable, V, S <: Signature[V]] {
  def signature: S
}

trait StateChangeReason

sealed trait TransactionValidationError[B <: BlockChain] extends ValidationError[B#BlockChainTransaction]

sealed trait BlockValidatorError[B <: BlockChain] extends ValidationError[B#BlockChainBlock]

abstract class TransactionValidator[B <: BlockChain] extends ValidatorOnBlockChain[B, B#BlockChainTransaction, TransactionValidationError[B]]

abstract class BlockValidator[B <: BlockChain] extends ValidatorOnBlockChain[B, B#BlockChainBlock, BlockValidatorError[B]]

abstract class AggregatedValidatorOnBlockchain[B <: BlockChain, V <: Validable, VE <: ValidationError[V]](validators: Seq[ValidatorOnBlockChain[B, V, VE]]) extends ValidatorOnBlockChain[B, V, VE] {
  // http://stackoverflow.com/questions/7230999/how-to-reduce-a-seqeithera-b-to-a-eithera-seqb
  private def sequence[A, B](s: Seq[Either[A, B]]): Either[Seq[A], B] =
    s.foldRight(Left(Nil): Either[List[A], B]) {
      (e, acc) => for (xs <- acc.left; x <- e.left) yield x :: xs
    }

  override def validate(validable: V)(implicit blockChain: B): Either[Seq[VE], V] = {
    sequence(validators.map(_.validate(validable)))
      .left.map(_.flatten)
  }
}

abstract class ValidatorOnBlockChain[B <: BlockChain, V <: Validable, E <: ValidationError[V]] {
  def validate(tx: V)(implicit blockChain: B): Either[Seq[E], V]
}

trait BlockChain {

  abstract class BlockChainBlock extends Validable with Signable {
    type Id
  }

  abstract class BlockChainTransaction extends Validable with Signable with StateChangeReason

  type T <: BlockChainTransaction
  type B <: BlockChainBlock
  type A <: BlockChainAccount

  abstract class BlockChainAccount(val publicKey: Array[Byte], val privateKey: Option[Array[Byte]])

  abstract class BlockChainAddress(val address: Array[Byte])

  trait BlockChainSignedTransaction[V] extends BlockChainTransaction with Signed[T, V, Signature[V]]

  trait BlockChainSignedBlock[V] extends BlockChainBlock with Signed[B, V, Signature[V]]

  type TransactionValidator = AggregatedValidatorOnBlockchain[this.type, BlockChainTransaction, TransactionValidationError[this.type]]
  type BlockValidator = AggregatedValidatorOnBlockchain[this.type, BlockChainBlock, BlockValidatorError[this.type]]

  def genesis: BlockChainBlock

  def txValidator: TransactionValidator

  def blockValidator: BlockValidator
}


abstract class Wallet[B <: BlockChain] {
  def createNewAccount: B#BlockChainAccount
}

abstract class BlockGenerator[B <: BlockChain] {
  def blocks: Observable[B#BlockChainBlock]
}

trait BlockChainApp[B <: BlockChain] {
  def blockChain: BlockChain

  def wallet: Wallet[B]

  def utx: B#BlockChainTransaction

  def miner: BlockGenerator[B]
}

// todo хранить блокчейн как дерево и удалять неосновные ветки после N
abstract class BlockStorage[B <: BlockChain] {
  def put(block: B#BlockChainBlock): Unit

  def get(id: B#BlockChainBlock#Id): Option[B#BlockChainBlock]

  def contains(id: B#BlockChainBlock#Id): Boolean
}

abstract class UnconfirmedTransactionStorage[B <: BlockChain] {
  def put(tx: B#BlockChainTransaction): Unit

  def all: Seq[B#BlockChainTransaction]

  def remove(tx: B#BlockChainTransaction): Option[B#BlockChainTransaction]
}

abstract class StateStorage[B <: BlockChain] {
  type BalanceAccount
  type Balance = Long

  def currentState: Map[BalanceAccount, Balance]

  def currentBalance(balanceAccount: BalanceAccount): Balance

  def apply(b: B#BlockChainBlock): Unit

  def rollback(b: B#BlockChainBlock): Unit
}

sealed trait ProtocolRequest

trait IncomingNetworkLayer[B <: BlockChain] {
  def incomingRequests: Observable[ProtocolRequest]

  def incomingTx: Observable[B#BlockChainTransaction]

  def incomingBlocks: Observable[B#BlockChainBlock]
}

trait OutgoingNetworkLayer[B <: BlockChain] {
  def outgoingRequests: Observable[ProtocolRequest]

  def outgoingTx: Observable[B#BlockChainTransaction]

  def outgoingBlocks: Observable[B#BlockChainBlock]
}

trait NetworkLayer[B <: BlockChain] extends IncomingNetworkLayer[B] with OutgoingNetworkLayer[B]
