package ru.tolsi.aobp.blockchain.waves.transaction

import ru.tolsi.aobp.blockchain.waves.{Asset, Waves, WavesMoney}

trait AssetIssuanceTransaction extends WavesTransaction {
  def issue: WavesMoney[Right[Waves.type, Asset]]

  def reissuable: Boolean
}
