package ru.tolsi.aobp.blockchain

import ru.tolsi.aobp.blockchain.base.LyHash
import ru.tolsi.aobp.blockchain.waves.block.{WavesSignedBlock, WavesBlock}
import ru.tolsi.aobp.blockchain.waves.transaction.{WavesSignedTransaction, WavesTransaction}

import scala.util.Either

package object waves {
  type Seed = Array[Byte]
  type PrivateKey = Array[Byte]
  type PublicKey = Array[Byte]

  type SignedTransaction[TX <: WavesTransaction] = WavesSignedTransaction[TX]
  type SignedBlock[BL <: WavesBlock] = WavesSignedBlock[BL]

  sealed trait WavesСurrency

  case object Waves extends WavesСurrency

  case class Asset(id: Array[Byte], decimals: Long) extends WavesСurrency {
    override def equals(obj: scala.Any): Boolean = obj match {
      case Asset(otherId, _) => id sameElements otherId
    }

    override def hashCode(): Int = {
      LyHash.compute(id)
    }
  }

  case class WavesMoney[C <: Either[Waves.type, Asset]](value: Long, currency: C) {
    def amount: BigDecimal = {
      val valueBigDecimal = BigDecimal(value)
      currency.fold(_ => valueBigDecimal, a => valueBigDecimal / (10 ^ a.decimals))
    }
  }
}
