package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves.{Asset, SignedTransaction, Waves, WavesBlockChain}
import scorex.crypto.encode.Base58

class TransactionsOrdering extends Ordering[SignedTransaction[WavesTransaction]] {
  private def orderBy(t: SignedTransaction[WavesTransaction]): (Long, Long, String) = {
    //TODO sort by real asset value of fee?
    val byFee = t.feeCurrency match {
      case Waves =>
        -t.fee
      case _: Asset =>
        0
    }
    val byTimestamp = -t.timestamp
    val byAddress = Base58.encode(t.signature.value)
    (byFee, byTimestamp, byAddress)
  }
  override def compare(first: SignedTransaction[WavesTransaction], second: SignedTransaction[WavesTransaction]): Int = {
    implicitly[Ordering[(Long, Long, String)]].compare(orderBy(first), orderBy(second))
  }
}
