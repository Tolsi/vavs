package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.block._
import ru.tolsi.aobp.blockchain.waves.block.validator.signedBlockValidator
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import ru.tolsi.aobp.blockchain.waves.storage.state.AbstractWavesStateStorage
import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves.transaction.validator.{WavesTransactionValidationParameters, signedTransactionWithTimeValidator}
import scorex.crypto.hash.Blake256

//private[waves]
abstract class WavesBlockChain {
  final type ST[TX <: WavesTransaction] = SignedTransaction[TX]
  final type SB[BL <: WavesBlock] = SignedBlock[BL]
//  final type AC = Account
//  final type AD = Address
//  type TXV = AbstractSignedTransactionValidator[WavesBlockChain, T, ST[T]]
//  type TVP = WavesTransactionValidationParameters
//  type SBV = AbstractSignedBlockValidator[WavesBlockChain, B, SB[B]]
//  type SS = AbstractWavesStateStorage

  final val secureHash = ScorexHashChain
  final val fastHash = Blake256

//  def stateStorage: SS = ???

  def configuration: WavesConfiguration

  def genesis: WavesBlock

  def txValidator(bvp: WavesTransactionValidationParameters): AbstractSignedTransactionValidator[WavesTransaction, Signed[WavesTransaction, ArrayByteSignature]] = signedTransactionWithTimeValidator(bvp.blockTimestamp)

  def blockValidator: AbstractSignedBlockValidator[WavesBlock] = signedBlockValidator

}

class TestNetWavesBlockChain extends WavesBlockChain with TestNetWavesBlockChainConfiguration
