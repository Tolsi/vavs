package ru.tolsi.aobp.blockchain.waves.model.transaction

import ru.tolsi.aobp.blockchain.waves.model.Currency._
import ru.tolsi.aobp.blockchain.waves.model._

case class ReissueTransaction(sender: Address,
                              issue: AssetVolume,
                              reissuable: Boolean,
                              feeMoney: WavesVolume,
                              timestamp: Timestamp)
  extends Transaction
