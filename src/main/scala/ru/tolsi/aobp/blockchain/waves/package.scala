package ru.tolsi.aobp.blockchain

import ru.tolsi.aobp.blockchain.base.LyHash

import scala.util.Either

package object waves {
  type Seed = Array[Byte]
  type PrivateKey = Array[Byte]
  type Address = Array[Byte]
  type PublicKey = Array[Byte]

  sealed trait WavesСurrency

  case object Waves extends WavesСurrency

  case class Asset(id: Array[Byte]) extends WavesСurrency {
    override def equals(obj: scala.Any): Boolean = obj match {
      case Asset(otherId) => id sameElements otherId
    }

    override def hashCode(): Int = {
      LyHash.compute(id)
    }
  }

  case class WavesMoney[C <: Either[Waves.type, Asset]](value: Long, currency: C)

}
