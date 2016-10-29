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

sealed trait TransactionValidationError[B <: BlockChain] extends ValidationError[B#Transaction]

sealed trait BlockValidatorError[B <: BlockChain] extends ValidationError[B#Block]

abstract class TransactionValidator[B <: BlockChain] extends ValidatorOnBlockChain[B, B#Transaction, TransactionValidationError[B]]

abstract class BlockValidator[B <: BlockChain] extends ValidatorOnBlockChain[B, B#Block, BlockValidatorError[B]]

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

  abstract class Block extends Validable with Signable {
    type Id
  }

  abstract class Transaction extends Validable with Signable with StateChangeReason

  type T <: Transaction
  type B <: Block
  type A <: Account

  abstract class Account(val publicKey: Array[Byte], val privateKey: Option[Array[Byte]])

  abstract class Address(val address: Array[Byte])

  trait SignedTransaction[V] extends Transaction with Signed[T, V, Signature[V]]

  trait SignedBlock[V] extends Block with Signed[B, V, Signature[V]]

  type TransactionValidator = AggregatedValidatorOnBlockchain[this.type, Transaction, TransactionValidationError[this.type]]
  type BlockValidator = AggregatedValidatorOnBlockchain[this.type, Block, BlockValidatorError[this.type]]

  def genesis: Block

  def txValidator: TransactionValidator

  def blockValidator: BlockValidator
}


abstract class Wallet[B <: BlockChain] {
  def createNewAccount: B#Account
}

abstract class BlockGenerator[B <: BlockChain] {
  def blocks: Observable[B#Block]
}

trait BlockChainApp[B <: BlockChain] {
  def blockChain: BlockChain

  def wallet: Wallet[B]

  def utx: B#Transaction

  def miner: BlockGenerator[B]
}

// todo хранить блокчейн как дерево и удалять неосновные ветки после N
abstract class BlockStorage[B <: BlockChain] {
  def put(block: B#Block): Unit

  def get(id: B#Block#Id): Option[B#Block]

  def contains(id: B#Block#Id): Boolean
}

abstract class UnconfirmedTransactionStorage[B <: BlockChain] {
  def put(tx: B#Transaction): Unit

  def all: Seq[B#Transaction]

  def remove(tx: B#Transaction): Option[B#Transaction]
}

abstract class StateStorage[B <: BlockChain] {
  type BalanceAccount
  type Balance = Long

  def currentState: Map[BalanceAccount, Balance]

  def currentBalance(balanceAccount: BalanceAccount): Balance

  def apply(b: B#Block): Unit

  def rollback(b: B#Block): Unit
}

sealed trait ProtocolRequest

trait IncomingNetworkLayer[B <: BlockChain] {
  def incomingRequests: Observable[ProtocolRequest]

  def incomingTx: Observable[B#Transaction]

  def incomingBlocks: Observable[B#Block]
}

trait OutgoingNetworkLayer[B <: BlockChain] {
  def outgoingRequests: Observable[ProtocolRequest]

  def outgoingTx: Observable[B#Transaction]

  def outgoingBlocks: Observable[B#Block]
}

trait NetworkLayer[B <: BlockChain] extends IncomingNetworkLayer[B] with OutgoingNetworkLayer[B]
