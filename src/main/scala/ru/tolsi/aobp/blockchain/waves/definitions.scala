package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.LyHash
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializable
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
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

sealed trait Validable

abstract class AbstractValidationError[V <: Validable](m: => String) {
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

abstract class BlockChainTransaction extends WithSign with Signable with Validable with StateChangeReason with BytesSerializable

trait BlockChainSignedTransaction[BlockChainTransaction, SI <: Signature[Array[Byte]]] extends BlockChainTransaction with Signed[BlockChainTransaction, SI] {

  protected trait ValidationError extends AbstractValidationError[BlockChainTransaction]

}

abstract class BlockChainBlock extends WithSign with Signable with Validable with StateChangeReason with BytesSerializable {
  type Id
}

trait BlockChainSignedBlock[BL <: BlockChainBlock, SI <: Signature[Array[Byte]]] extends BlockChainBlock with Signed[BL, SI] {

  protected trait ValidationError extends AbstractValidationError[BlockChainBlock]

}

case class StateChange(account: BalanceAccount, amount: Long)

abstract class BlockChainAccount(val publicKey: Array[Byte], val privateKey: Option[Array[Byte]])

abstract class BlockChainAddress(val address: Array[Byte]) extends Validable

abstract class TransactionValidationError[+TX <: BlockChainTransaction](message: => String) extends AbstractValidationError[TX](message)

abstract class BlockValidationError[+BL <: BlockChainBlock](message: => String) extends AbstractValidationError[BlockChainBlock](message)

abstract class TransactionValidator[TX <: BlockChainTransaction] extends ValidatorOnBlockChain[TX, BlockChainTransaction, TransactionValidationError[TX]]

abstract class AbstractSignedTransactionValidator[TX <: BlockChainTransaction, STX <: Signed[TX, Signature64]] extends ValidatorOnBlockChain[STX, BlockChainTransaction, TransactionValidationError[BlockChainTransaction]]

abstract class AbstractBlockValidator[BL <: BlockChainBlock] extends ValidatorOnBlockChain[BL, BlockChainBlock, BlockValidationError[BlockChainBlock]]
abstract class AbstractSignedBlockValidator[BL <: BlockChainBlock] extends ValidatorOnBlockChain[Signed[BL, Signature64], BlockChainBlock, BlockValidationError[BlockChainBlock]]

trait BlockTransactionParameters

abstract class Wallet {
  def createNewAccount: BlockChainAccount
}

abstract class BlockGenerator {
  def blocks: Observable[BlockChainBlock]
}

trait BlockChainApp {
  def blockChain: WavesBlockChain

  def wallet: Wallet

  def utx: BlockChainTransaction

  def miner: BlockGenerator
}

// todo хранить блокчейн как дерево и удалять неосновные ветки после N
abstract class BlockStorage[BSB <: Signed[BlockChainTransaction, Signature64], BId <: BlockChainBlock#Id] {
  type BlockId = BId
  type SignedBlock = BSB
  def put(block: SignedBlock): Unit

  def get(id: BlockId): Option[SignedBlock]

  def contains(id: BlockId): Boolean

  def remove(id: BlockId): Option[SignedBlock]
}

abstract class UnconfirmedTransactionStorage {
  def put(tx: BlockChainTransaction): Unit

  def all: Seq[BlockChainTransaction]

  def remove(tx: BlockChainTransaction): Option[BlockChainTransaction]
}

abstract class StateStorage[SignedBlock <: Signed[BlockChainTransaction, Signature64], BBA <: BlockChainAccount] {
  type BalanceValue = Long

  def currentState: Map[BBA, BalanceValue]

  def currentBalance(balanceAccount: BBA): Option[BalanceValue]

  def add(b: SignedBlock): Unit

  def switchTo(b: SignedBlock): Unit

  def lastBlock: SignedBlock
}

trait StateValidator {
  def isLeadToValidState(b: WavesBlock): Boolean
  def isLeadToValidState(t: WavesTransaction): Boolean
  def isLeadToValidState(t: Seq[WavesTransaction]): Boolean
}

sealed trait ProtocolRequest

trait IncomingNetworkLayer {
  def incomingRequests: Observable[ProtocolRequest]

  def incomingTx: Observable[BlockChainTransaction]

  def incomingBlocks: Observable[BlockChainBlock]
}

trait OutgoingNetworkLayer {
  def outgoingRequests: Observable[ProtocolRequest]

  def outgoingTx: Observable[BlockChainTransaction]

  def outgoingBlocks: Observable[BlockChainBlock]
}

trait NetworkLayer extends IncomingNetworkLayer with OutgoingNetworkLayer
