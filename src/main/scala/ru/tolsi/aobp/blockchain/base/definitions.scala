package ru.tolsi.aobp.blockchain.base

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializable
import rx.Observable
import scorex.crypto.encode.Base58

trait Sign[V]
case class ArraySign(value: Array[Byte]) extends Sign[Array[Byte]]
trait WithArraySign
trait ArraySignCreator[WAS <: WithArraySign] {
  def createSign(ws: WAS): ArraySign
}
trait WithByteArraySing extends WithArraySign

trait Signable

abstract class Signature[V] {
  def value: V
}

abstract class Signer[BC <: BlockChain, S <: Signable with WithByteArraySing, SI <: Signature[Array[Byte]]] {
  def sign(obj: S): Signed[S, SI]
}

abstract class ArrayByteSignature extends Signature[Array[Byte]] {
  override def hashCode(): Int = {
    LyHash.compute(value)
  }

  override def equals(obj: scala.Any): Boolean = obj match {
    case as: ArrayByteSignature => as.value sameElements value
    case _ => false
  }

  override def toString: String = Base58.encode(value)
}

class Signature32(val value: Array[Byte]) extends Signature[Array[Byte]] {
  require(value.length == 32, "Signature32 should have 32 bytes")
}

class Signature64(val value: Array[Byte]) extends Signature[Array[Byte]] {
  require(value.length == 64, "Signature64 should have 64 bytes")
}

sealed trait Validable

abstract class AbstractValidationError[+V <: Validable](m: => String) {
  def message: String = m
}

trait Signed[+S <: Signable with WithByteArraySing, SI <: Signature[Array[Byte]]] {
  def signature: SI
  def signed: S
}

trait StateChangeReason

// todo VE, E <: AbstractValidationError[VE] ?
abstract class ValidatorOnBlockChain[BC <: BlockChain, V <: Validable, E <: AbstractValidationError[V]] {
  def validate(tx: V): Either[Seq[E], V]
}

trait BlockChain {
  protected abstract class BlockChainTransaction extends WithByteArraySing with Signable with Validable with StateChangeReason with BytesSerializable

  protected trait BlockChainSignedTransaction[TX <: T, SI <: Signature[Array[Byte]]] extends BlockChainTransaction with Signed[TX, SI] {

    protected trait ValidationError extends AbstractValidationError[this.type]

  }

  protected abstract class BlockChainBlock extends WithByteArraySing with Signable with Validable with StateChangeReason with BytesSerializable {
    type Id
  }

  protected trait BlockChainSignedBlock[BL <: B, SI <: Signature[Array[Byte]]] extends BlockChainBlock with Signed[BL, SI] {

    protected trait ValidationError extends AbstractValidationError[this.type]

  }

  trait BlockTransactionParameters

  type T <: BlockChainTransaction
  type ST[TX <: T] <: Signed[TX, Signature64] with T
  type B <: BlockChainBlock
  type SB[BL <: B] <: Signed[BL, Signature64] with B
  type AС <: BlockChainAccount
  type AD <: BlockChainAddress
  type TXV <: AbstractSignedTransactionValidator[T, ST[T]]
  type TVP <: BlockTransactionParameters
  type SBV <: AbstractSignedBlockValidator[B, SB[B]]
  type BA

  case class StateChange(account: BA, amount: Long)

  protected abstract class BlockChainAccount(val publicKey: Array[Byte], val privateKey: Option[Array[Byte]])

  protected abstract class BlockChainAddress(val address: Array[Byte]) extends Validable

  abstract class TransactionValidationError[+TX <: T](message: => String) extends AbstractValidationError[TX](message)

  abstract class BlockValidationError[+BL <: B](message: => String) extends AbstractValidationError[BL](message)
  abstract class SignedBlockValidationError[+BL <: B](message: => String) extends BlockValidationError[BL](message)

  protected abstract class TransactionValidator[TX <: T] extends ValidatorOnBlockChain[this.type, TX, TransactionValidationError[TX]]

  protected abstract class AbstractSignedTransactionValidator[TX <: T, STX <: ST[TX]] extends ValidatorOnBlockChain[this.type, STX, TransactionValidationError[STX]]

  protected abstract class AbstractBlockValidator[BL <: B] extends ValidatorOnBlockChain[this.type, BL,  BlockValidationError[BL]]
  protected abstract class AbstractSignedBlockValidator[BL <: B, SBL <: SB[BL]] extends ValidatorOnBlockChain[this.type, SBL, SignedBlockValidationError[SBL]]

  protected def genesis: B

  protected def txValidator(bvp: TVP): TXV

  protected def blockValidator: SBV
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
  final type SignedBlock = BC#SB[BC#B]
  final type BlockId = BC#B#Id

  def put(block: SignedBlock): Unit

  def get(id: BlockId): Option[SignedBlock]

  def contains(id: BlockId): Boolean

  def remove(id: BlockId): Option[SignedBlock]
}

abstract class UnconfirmedTransactionStorage[BC <: BlockChain] {
  def put(tx: BC#T): Unit

  def all: Seq[BC#T]

  def remove(tx: BC#T): Option[BC#T]
}

abstract class StateStorage[BC <: BlockChain] {
  type BalanceValue = Long
  final type SignedBlock = BC#SB[BC#B]

  def currentState: Map[BC#BA, BalanceValue]

  def currentBalance(balanceAccount: BC#BA): Option[BalanceValue]

  def add(b: SignedBlock): Unit

  def isValid(stateChanges: Seq[BC#StateChange]): Boolean

  def switchTo(b: SignedBlock): Unit
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
