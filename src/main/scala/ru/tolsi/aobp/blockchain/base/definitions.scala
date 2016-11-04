package ru.tolsi.aobp.blockchain.base

import rx.Observable

trait Signable

abstract class Signature[V] {
  def value: V
}

abstract class Signer[BC <: BlockChain, S <: Signable, V, SI <: Signature[V]] {
  def sign(obj: S): Signed[S, V, SI]
}

class Signature32[V](val value: V) extends Signature[V]

class Signature64[V](val value: V) extends Signature[V]

sealed trait Validable

abstract class AbstractValidationError[+V <: Validable](m: => String) {
  def message: String = m
}

trait Signed[+S <: Signable, V, SI <: Signature[V]] {
  def signature: SI
  def signed: S
}

trait StateChangeReason

// todo VE, E <: AbstractValidationError[VE] ?
abstract class ValidatorOnBlockChain[BC <: BlockChain, V <: Validable, E <: AbstractValidationError[V]] {
  def validate(tx: V): Either[Seq[E], V]
}

trait BlockChain {

  protected abstract class BlockChainBlock extends Validable with Signable {
    type Id
  }

  protected abstract class BlockChainTransaction extends Signable with Validable with StateChangeReason

  protected trait BlockChainSignedTransaction[TX <: T, V, SI <: Signature[V]] extends BlockChainTransaction with Signed[TX, V, SI]
    with Validable {

    protected trait ValidationError extends AbstractValidationError[this.type]

  }

  protected trait BlockChainSignedBlock[BL <: B, V, SI <: Signature[V]] extends BlockChainBlock with Validable with Signed[BL, V, SI] {

    protected trait ValidationError extends AbstractValidationError[this.type]

  }

  trait BlockTransactionParameters

  type T <: BlockChainTransaction
  type ST[TX <: T] <: Signed[TX, Array[Byte], Signature64[Array[Byte]]] with T
  type B <: BlockChainBlock
  type SB[BL <: B] <: Signed[BL, Array[Byte], Signature64[Array[Byte]]] with B
  type AС <: BlockChainAccount
  type AD <: BlockChainAddress
  type TXV <: AbstractSignedTransactionValidator[T, ST[T]]
  type TVP <: BlockTransactionParameters

  protected abstract class BlockChainAccount(val publicKey: Array[Byte], val privateKey: Option[Array[Byte]])

  protected abstract class BlockChainAddress(val address: Array[Byte]) extends Validable

  abstract class TransactionValidationError[+TX <: T](message: => String) extends AbstractValidationError[TX](message)

  abstract class BlockValidationError[+BL <: B](message: => String) extends AbstractValidationError[BL](message)
  abstract class SignedBlockValidationError[+BL <: B](message: => String) extends BlockValidationError[BL](message)

  protected abstract class TransactionValidator[TX <: T] extends ValidatorOnBlockChain[this.type, TX, TransactionValidationError[TX]]

  protected abstract class AbstractSignedTransactionValidator[TX <: T, STX <: ST[TX]] extends ValidatorOnBlockChain[this.type, STX, TransactionValidationError[STX]]

  protected abstract class BlockValidator[BL <: B] extends ValidatorOnBlockChain[this.type, BL,  BlockValidationError[BL]]
  protected abstract class SignedBlockValidator[BL <: B, SBL <: SB[BL]] extends ValidatorOnBlockChain[this.type, SBL, SignedBlockValidationError[SBL]]

  protected def genesis: B

  protected def txValidator(bvp: TVP): TXV

  protected def blockValidator: SignedBlockValidator[B, SB[B]]
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
