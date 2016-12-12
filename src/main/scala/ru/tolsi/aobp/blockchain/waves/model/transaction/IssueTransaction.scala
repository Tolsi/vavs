package ru.tolsi.aobp.blockchain.waves.model.transaction

import ru.tolsi.aobp.blockchain.waves.model.Currency._
import ru.tolsi.aobp.blockchain.waves.model._

case class IssueTransaction(sender: Address,
                            name: Array[Byte],
                            description: Array[Byte],
                            issue: AssetVolume,
                            decimals: Byte,
                            reissuable: Boolean,
                            feeMoney: WavesVolume,
                            timestamp: Timestamp)
  extends Transaction
