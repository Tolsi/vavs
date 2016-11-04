package ru.tolsi.aobp.blockchain.waves

trait WavesUnconfirmedTransactionsValidators {
  self: WavesBlockChain =>
  override protected def utxValidator: SignedTransactionValidator[T, ST[T]]
}
