package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.crypto.ScorexHashChain
import scorex.crypto.hash.Blake256

private[waves] abstract class WavesBlockChain extends BlockChain
  with WavesTransactions
  with WavesTransactionsSigners
  with WavesTransactionsValidators
  with WavesUnconfirmedTransactionsValidators
  with WavesAccounts
  with WavesBlocks
  with WavesBlocksValidators {
  def chainId: Byte

  final type T = Transaction
  final type ST[TX <: T] = SignedTransaction[TX]
  final type B = Block
  final type SB[BL <: B] = Nothing
  //BlockChainSignedBlock[BL, Array[Byte], ArraySignature64]
  final type AС = Account
  final type AD = Address

  final val secureHash = ScorexHashChain
  final val fastHash = Blake256

  def state: StateStorage[this.type]

  def blocksStorage: BlockStorage[this.type]
}
