package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.block._
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import ru.tolsi.aobp.blockchain.waves.storage.state.AbstractWavesStateStorage
import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves.transaction.validator.{SignedTransactionWithTimeValidator, WavesTransactionValidationParameters, signedTransactionWithTimeValidator}
import ru.tolsi.aobp.blockchain.waves.block.validator.signedBlockValidator
import scorex.crypto.hash.Blake256

private[waves] abstract class WavesBlockChain extends BlockChain {
  final type T = WavesTransaction
  final type ST[TX <: T] = SignedTransaction[TX]
  final type B = WavesBlock
  final type SB[BL <: B] = SignedBlock[BL]
  final type AC = Account
  final type AD = Address
  type TXV = AbstractSignedTransactionValidator[this.type, T, ST[T]]
  type TVP = WavesTransactionValidationParameters
  type SBV = AbstractSignedBlockValidator[this.type, B, SB[B]]
  type SS = AbstractWavesStateStorage
  type BA = (Address, WavesСurrency)

  final val secureHash = ScorexHashChain
  final val fastHash = Blake256

  def stateStorage: SS

  def configuration: WavesConfiguration

  override def txValidator(bvp: TVP): SignedTransactionWithTimeValidator = signedTransactionWithTimeValidator(bvp.blockTimestamp)

  override val blockValidator: AbstractSignedBlockValidator[WavesBlockChain, WavesBlock, SB[WavesBlock]] = signedBlockValidator
}
