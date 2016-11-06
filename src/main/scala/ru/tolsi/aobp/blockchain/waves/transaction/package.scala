package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{Signature64, Signed}
import ru.tolsi.aobp.blockchain.waves.transaction.signer.wavesTransactionSigner

package object transaction {
  implicit class SignTransaction[TX <: WavesBlockChain#T](t: TX) extends AnyRef {
    def signed(implicit wbc: WavesBlockChain): SignedTransaction[WavesBlockChain#T] = wavesTransactionSigner.sign(t)
  }
  implicit val transactionsOrdering = new TransactionsOrdering
}
