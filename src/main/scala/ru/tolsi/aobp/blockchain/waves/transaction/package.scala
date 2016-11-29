package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.waves.transaction.signer.wavesTransactionSigner

package object transaction {
  implicit class SignTransaction[TX <: WavesTransaction](t: TX) extends AnyRef {
    def signed(implicit wbc: WavesBlockChain): WavesSignedTransaction[WavesTransaction] = wavesTransactionSigner.sign(t)
  }
  implicit val transactionsOrdering = new TransactionsOrdering
}
