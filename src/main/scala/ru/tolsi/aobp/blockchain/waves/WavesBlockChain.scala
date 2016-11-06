package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.block._
import ru.tolsi.aobp.blockchain.waves.block.validator.signedBlockValidator
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import ru.tolsi.aobp.blockchain.waves.storage.state.AbstractWavesStateStorage
import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves.transaction.validator.{WavesTransactionValidationParameters, signedTransactionWithTimeValidator}
import scorex.crypto.hash.Blake256

private[waves] abstract class WavesBlockChain extends BlockChain {
  final type T = WavesTransaction
  final type ST[TX <: T] = SignedTransaction[TX]
  final type B = WavesBlock
  final type SB[BL <: B] = SignedBlock[BL]
  final type AC = Account
  final type AD = Address
  type TXV = AbstractSignedTransactionValidator[WavesBlockChain, T, ST[T]]
  type TVP = WavesTransactionValidationParameters
  type SBV = AbstractSignedBlockValidator[WavesBlockChain, B, SB[B]]
  type SS = AbstractWavesStateStorage
  type BA = (Address, WavesСurrency)

  final val secureHash = ScorexHashChain
  final val fastHash = Blake256

  def stateStorage: SS = ???

  def configuration: WavesConfiguration

  def genesis: WavesBlock

  def txValidator(bvp: WavesTransactionValidationParameters): AbstractSignedTransactionValidator[WavesBlockChain, WavesTransaction, ST[T]] = signedTransactionWithTimeValidator(bvp.blockTimestamp)

  def blockValidator: AbstractSignedBlockValidator[WavesBlockChain, WavesBlock, SB[B]] = signedBlockValidator

}

class TestNetWavesBlockChain extends WavesBlockChain with TestNetWavesBlockChainConfiguration
