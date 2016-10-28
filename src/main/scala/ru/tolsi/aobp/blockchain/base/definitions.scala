package ru.tolsi.aobp.blockchain.base

import rx.Observable
import scorex.crypto.hash.CryptographicHash

sealed trait Validable

sealed trait ValidationError[V <: Validable]

abstract class Block(id: Block#Id) extends Validable {
  type Id = String
}

abstract class Transaction(id: Transaction#Id) extends Validable {
  type Id = String
}

abstract class BlockGenerator {
  def blocks: Observable[Block]
}

sealed trait TransactionValidationError[T <: Transaction] extends ValidationError[T]

class AggregatedValidatorOnBlockchain[V <: Validable, VE <: ValidationError[V], T <: Transaction, B <: Block, BC <: BlockChain[B, T]](validators: Seq[ValidatorOnBlockchain[V, T, VE, B, BC]]) extends ValidatorOnBlockchain[V, T, VE, B, BC] {
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

abstract class TransactionValidator[T <: Transaction, B <: Block, BC <: BlockChain[B, T]] extends ValidatorOnBlockchain[T, T, TransactionValidationError[T], B, BC]

sealed trait BlockValidatorError[B <: Block] extends ValidationError[B]

abstract class BlockValidator[T <: Transaction, B <: Block, BC <: BlockChain[B, T]] extends ValidatorOnBlockchain[B, T, BlockValidatorError[B], B, BC]

abstract class ValidatorOnBlockchain[V <: Validable, T <: Transaction, E <: ValidationError[V], B <: Block, BC <: BlockChain[B, T]] {
  def validate(tx: V)(implicit blockChain: BC): Either[Seq[E], V]
}

// todo хранить блокчейн как дерево и удалять неосновные ветки после N
abstract class BlockStorage[B <: Block] {
  def put(block: Block): Unit

  def get(id: Block#Id): B

  def contains(id: Block#Id): Boolean
}

abstract class TransactionStorage[T <: Transaction] {
  def put(block: Block): Unit

  def get(id: Transaction#Id): T

  def contains(id: Transaction#Id): Boolean
}

abstract class Account

abstract class Wallet {
  def createNewAccount: Account
}

trait BlockChain[B <: Block, T <: Transaction] {
  def genesis: Block

  def wallet: Wallet

  def storage: BlockStorage[B]

  def utx: Seq[T]

  def miner: BlockGenerator

  def hashes: Seq[CryptographicHash]

  def txValidator: AggregatedValidatorOnBlockchain[T, TransactionValidationError[T], T, B, this.type]

  def blockValidator: AggregatedValidatorOnBlockchain[B, BlockValidatorError[B], T, B, this.type]
}

trait IncomingNetworkLayer {
  def incomingTx: Observable[Transaction]
  def incomingBlocks: Observable[Block]
}

trait OutgoingNetworkLayer {
  def outcomingTx: Observable[Transaction]
  def outcomingBlocks: Observable[Block]
}

trait NetworkLayer extends IncomingNetworkLayer with OutgoingNetworkLayer
