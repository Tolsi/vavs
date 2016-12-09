package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.block._
import ru.tolsi.aobp.blockchain.waves.block.validator.signedBlockValidator
import ru.tolsi.aobp.blockchain.waves.crypto.SecureHashChain
import ru.tolsi.aobp.blockchain.waves.storage.state.AbstractWavesStateStorage
import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves.transaction.validator.{WavesTransactionValidationParameters, signedTransactionWithTimeValidator}

abstract class WavesBlockChain {
  def stateStorage: AbstractWavesStateStorage = ???

  def configuration: WavesConfiguration

  def genesis: WavesBlock

  def txValidator(bvp: WavesTransactionValidationParameters): AbstractSignedTransactionValidator[WavesTransaction, WavesSignedTransaction[WavesTransaction]] = signedTransactionWithTimeValidator(bvp.blockTimestamp)

  def blockValidator: AbstractSignedBlockValidator[WavesBlock, WavesSignedBlock[WavesBlock]] = signedBlockValidator

}

class TestNetWavesBlockChain extends WavesBlockChain with TestNetWavesBlockChainConfiguration
