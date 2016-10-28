package ru.tolsi.aobp.blockchain.base

import rx.Observable
import scorex.crypto.hash.CryptographicHash

trait Signable

sealed trait Signature

class Signature32 extends Signature
class Signature64 extends Signature

sealed trait Validable

sealed trait ValidationError[V <: Validable]

case class Signed[T <: Signable, S <: Signature](value: T, signature: S)

abstract class Block[BC <: BlockChain[T,Block[BC, T]], T <: Transaction[BC]](id: Block[BC, T]#Id, timestamp: Long) extends Validable with Signable {
  type Id = Array[Byte]
}

sealed trait ProtocolRequest

abstract class Transaction[BC <: BlockChain[_,_]](id: Transaction[BC]#Id, timestamp: Long, amount: Long, fee: Long) extends Validable with Signable {
  type Id = Array[Byte]
}

abstract class BlockGenerator[BC <: BlockChain[_,_], T <: Transaction[BC], B <: Block[BC, T]] {
  def blocks: Observable[B]
}

sealed trait TransactionValidationError[BC <: BlockChain[_,_], T <: Transaction[BC]] extends ValidationError[T]

class AggregatedValidatorOnBlockchain[V <: Validable, VE <: ValidationError[V], T <: Transaction[BC], B <: Block[BC, T], BC <: BlockChain[B, T]](validators: Seq[ValidatorOnBlockChain[V, T, VE, B, BC]]) extends ValidatorOnBlockChain[V, T, VE, B, BC] {
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

abstract class TransactionValidator[T <: Transaction[BC], B <: Block[BC, T], BC <: BlockChain[B, T]] extends ValidatorOnBlockChain[T, T, TransactionValidationError[BC, T], B, BC]

sealed trait BlockValidatorError[T <: Transaction[BC], B <: Block[BC, T], BC <: BlockChain[B, T]] extends ValidationError[B]

abstract class BlockValidator[T <: Transaction[BC], B <: Block[BC, T], BC <: BlockChain[B, T]] extends ValidatorOnBlockChain[B, T, BlockValidatorError[T, B, BC], B, BC]

abstract class ValidatorOnBlockChain[V <: Validable, T <: Transaction[BC], E <: ValidationError[V], B <: Block[BC, T], BC <: BlockChain[B, T]] {
  def validate(tx: V)(implicit blockChain: BC): Either[Seq[E], V]
}

// todo хранить блокчейн как дерево и удалять неосновные ветки после N
abstract class BlockStorage[T <: Transaction[BC], B <: Block[BC, T], BC <: BlockChain[B, T]] {
  def put(block: B): Unit

  def get(id: B#Id): B

  def contains(id: B#Id): Boolean
}

abstract class UnconfirmedTransactionStorage[T <: Transaction[BC], BC <: BlockChain[_, T]] {
  def put(tx: T): Unit

  def all: Seq[T]

  def remove(tx: T): Option[T]
}

abstract class AccountWithAddress[BC <: BlockChain[_,_]](address: String)

abstract class Wallet[BC <: BlockChain[_,_]] {
  def createNewAccount: AccountWithAddress[BC]
}

trait BlockChain[T <: Transaction[BlockChain[T, B]], B <: Block[BlockChain[T, B],T]] {
  type TxValidator = AggregatedValidatorOnBlockchain[T, BlockValidatorError[T, B, this.type], T, B, this.type]
  type BlockValidator = AggregatedValidatorOnBlockchain[B, BlockValidatorError[T, B, this.type], T, B, this.type]

  def genesis: Block[this.type, T]

  def txValidator: TxValidator

  def blockValidator: BlockValidator
}

trait BlockChainApp[B <: Block[BC, T], T <: Transaction[BC], BC <: BlockChain[B, T]] {
  def blockChain: BC

  def wallet: Wallet[BC]

  def storage: BlockStorage[T, B, BC]

  def utx: UnconfirmedTransactionStorage[T, BC]

  def miner: BlockGenerator[BC, T, B]

  def hashes: Seq[CryptographicHash]

  def txValidator: AggregatedValidatorOnBlockchain[T, TransactionValidationError[BC, T], T, B, this.type]

  def blockValidator: AggregatedValidatorOnBlockchain[B, BlockValidatorError[T, B, BC], T, B, this.type]
}

trait IncomingNetworkLayer[T <: Transaction[BC], B <: Block[BC, T], BC <: BlockChain[B, T]] {
  def incomingRequests: Observable[ProtocolRequest]
  def incomingTx: Observable[T]
  def incomingBlocks: Observable[B]
}

trait OutgoingNetworkLayer[T <: Transaction[BC], B <: Block[BC, T], BC <: BlockChain[B, T]] {
  def outgoingRequests: Observable[ProtocolRequest]
  def outgoingTx: Observable[T]
  def outgoingBlocks: Observable[B]
}

trait NetworkLayer[B <: Block[BC, T], T <: Transaction[BC], BC <: BlockChain[B, T]] extends IncomingNetworkLayer[B, T, BC] with OutgoingNetworkLayer[B, T, BC]
