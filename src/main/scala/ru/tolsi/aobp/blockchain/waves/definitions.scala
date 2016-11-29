package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.LyHash
import ru.tolsi.aobp.blockchain.waves.block.{WavesBlock, WavesSignedBlock}
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction
import rx.Observable
import scorex.crypto.encode.Base58

case class BalanceAccount(address: Address, currency: WavesСurrency)

case class Sign[+WS <: WithSign](value: Array[Byte])

trait WithSign

trait SignCreator[WS <: WithSign] {
  def createSign(ws: WS): Sign[WS]
}

trait Signable

abstract class Signature[V] {
  def value: V
}

abstract class Signer[S <: Signable with WithSign, SV <: Signed[S, SI], SI <: Signature[Array[Byte]]] {
  def sign(obj: S)(implicit bc: WavesBlockChain): SV
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

trait Validable

abstract class AbstractValidationError[+V <: Validable](m: => String) {
  def message: String = m
}

trait Signed[+S <: Signable with WithSign, SI <: Signature[Array[Byte]]] {
  def signature: SI
  def signed: S
}

trait StateChangeReason

// todo VE, E <: AbstractValidationError[VE] ?
abstract class ValidatorOnBlockChain[V <: Validable, VO >: V <: Validable, E <: AbstractValidationError[VO]] {
  type ResultT = Either[ErrorsSeqT, ValidatedT]
  type ErrorsSeqT = Seq[E]
  type ValidatedT = VO
  def validate(tx: V)(implicit bc: WavesBlockChain): ResultT
}

case class StateChange(account: BalanceAccount, amount: Long)

abstract class TransactionValidationError[+TX <: WavesTransaction](message: => String) extends AbstractValidationError[TX](message)

abstract class BlockValidationError[+BL <: WavesBlock](message: => String) extends AbstractValidationError[WavesBlock](message)

abstract class AbstractTransactionValidator[TX <: WavesTransaction] extends ValidatorOnBlockChain[TX, WavesTransaction, TransactionValidationError[TX]]

abstract class AbstractSignedTransactionValidator[TX <: WavesTransaction, STX <: Signed[TX, Signature64] with WavesTransaction] extends ValidatorOnBlockChain[STX, WavesTransaction, TransactionValidationError[WavesTransaction]]

abstract class AbstractBlockValidator[BL <: WavesBlock] extends ValidatorOnBlockChain[BL, WavesBlock, BlockValidationError[WavesBlock]]
abstract class AbstractSignedBlockValidator[BL <: WavesBlock, SBL <: Signed[BL, Signature64] with WavesBlock] extends ValidatorOnBlockChain[SBL, WavesBlock, BlockValidationError[WavesBlock]]

trait BlockTransactionParameters

abstract class Wallet {
  def createNewAccount: Account
}

abstract class BlockGenerator {
  def blocks: Observable[WavesBlock]
}

trait BlockChainApp {
  def blockChain: WavesBlockChain

  def wallet: Wallet

  def utx: WavesTransaction

  def miner: BlockGenerator
}

// todo хранить блокчейн как дерево и удалять неосновные ветки после N
abstract class BlockStorage[BSB <: Signed[WavesBlock, Signature64], BId <: WavesBlock#Id] {
  type BlockId = BId
  type SignedBlock = BSB
  def put(block: SignedBlock): Unit

  def get(id: BlockId): Option[SignedBlock]

  def contains(id: BlockId): Boolean

  def remove(id: BlockId): Option[SignedBlock]
}

abstract class UnconfirmedTransactionStorage {
  def put(tx: WavesTransaction): Unit

  def all: Seq[WavesTransaction]

  def remove(tx: WavesTransaction): Option[WavesTransaction]
}

abstract class StateStorage {
  type BalanceValue = Long

  def currentState: Map[BalanceAccount, BalanceValue]

  def currentBalance(balanceAccount: BalanceAccount): Option[BalanceValue]

  def add(b: WavesSignedBlock[WavesBlock]): Unit

  def switchTo(b: WavesSignedBlock[WavesBlock]): Unit

  def lastBlock: WavesSignedBlock[WavesBlock]
}

trait StateValidator {
  def isLeadToValidState(b: WavesBlock): Boolean
  def isLeadToValidState(t: WavesTransaction): Boolean
  def isLeadToValidState(t: Seq[WavesTransaction]): Boolean
}

sealed trait ProtocolRequest

trait IncomingNetworkLayer {
  def incomingRequests: Observable[ProtocolRequest]

  def incomingTx: Observable[WavesTransaction]

  def incomingBlocks: Observable[WavesBlock]
}

trait OutgoingNetworkLayer {
  def outgoingRequests: Observable[ProtocolRequest]

  def outgoingTx: Observable[WavesTransaction]

  def outgoingBlocks: Observable[WavesBlock]
}

trait NetworkLayer extends IncomingNetworkLayer with OutgoingNetworkLayer
