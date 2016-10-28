package ru.tolsi.aobp.blockchain.base

import rx.Observable
import scorex.crypto.hash.CryptographicHash

sealed trait Validable

sealed trait ValidationError[V <: Validable]

abstract class Block(id: Block#Id) extends Validable {
  type Id = Array[Byte]
}

sealed trait ProtocolRequest
abstract class Transaction(id: Transaction#Id) extends Validable {
  type Id = Array[Byte]
}

abstract class BlockGenerator[B <: Block] {
  def blocks: Observable[B]
}

sealed trait TransactionValidationError[T <: Transaction] extends ValidationError[T]

class AggregatedValidatorOnBlockchain[V <: Validable, VE <: ValidationError[V], T <: Transaction, B <: Block, BC <: BlockChain[B, T]](validators: Seq[ValidatorOnBlockChain[V, T, VE, B, BC]]) extends ValidatorOnBlockChain[V, T, VE, B, BC] {
  // http://stackoverflow.com/questions/7230999/how-to-reduce-a-seqeithera-b-to-a-eithera-seqb
  private def sequence[A, B](s: Seq[Either[A, B]]): Either[Seq[A], B] =
    s.foldRight(Left(Nil): Either[List[A], B]) {
      (e, acc) => for (xs <- acc.left; x <- e.left) yield x :: xs
    }

  override def validate(validable: V)(implicit blockChain: BC): Either[Seq[VE], V] = {
    sequence(validators.map(_.validate(validable)))
      .left.map(_.flatten)
  }
}

abstract class TransactionValidator[T <: Transaction, B <: Block, BC <: BlockChain[B, T]] extends ValidatorOnBlockChain[T, T, TransactionValidationError[T], B, BC]

sealed trait BlockValidatorError[B <: Block] extends ValidationError[B]

abstract class BlockValidator[T <: Transaction, B <: Block, BC <: BlockChain[B, T]] extends ValidatorOnBlockChain[B, T, BlockValidatorError[B], B, BC]

abstract class ValidatorOnBlockChain[V <: Validable, T <: Transaction, E <: ValidationError[V], B <: Block, BC <: BlockChain[B, T]] {
  def validate(tx: V)(implicit blockChain: BC): Either[Seq[E], V]
}

// todo хранить блокчейн как дерево и удалять неосновные ветки после N
abstract class BlockStorage[B <: Block] {
  def put(block: B): Unit

  def get(id: Block#Id): B

  def contains(id: Block#Id): Boolean
}

abstract class UnconfirmedTransactionStorage[T <: Transaction] {
  def put(tx: T): Unit

  def all: Seq[T]

  def remove(tx: T): Option[T]
}

abstract class Account[BC <: BlockChain[_,_]]

abstract class Wallet[BC <: BlockChain[_,_]] {
  def createNewAccount: Account[BC]
}

trait BlockChain[B <: Block, T <: Transaction] {
  def genesis: Block

  def hashes: Array[CryptographicHash]

  def txValidator: AggregatedValidatorOnBlockchain[T, TransactionValidationError[T], T, B, this.type]

  def blockValidator: AggregatedValidatorOnBlockchain[B, BlockValidatorError[B], T, B, this.type]
}


trait BlockChainApp[B <: Block, T <: Transaction, BC <: BlockChain[B, T]] {
  def blockChain: BC

  def wallet: Wallet[BC]

  def storage: BlockStorage[B]

  def utx: UnconfirmedTransactionStorage[T]

  def miner: BlockGenerator[B]

  def hashes: Seq[CryptographicHash]

  def txValidator: AggregatedValidatorOnBlockchain[T, TransactionValidationError[T], T, B, this.type]

  def blockValidator: AggregatedValidatorOnBlockchain[B, BlockValidatorError[B], T, B, this.type]
}

trait IncomingNetworkLayer {
  def incomingRequests: Observable[ProtocolRequest]
  def incomingTx: Observable[Transaction]
  def incomingBlocks: Observable[Block]
}

trait OutgoingNetworkLayer {
  def outgoingRequests: Observable[ProtocolRequest]
  def outgoingTx: Observable[Transaction]
  def outgoingBlocks: Observable[Block]
}

trait NetworkLayer extends IncomingNetworkLayer with OutgoingNetworkLayer
