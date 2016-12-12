package ru.tolsi.aobp.blockchain.waves.model.transaction

import ru.tolsi.aobp.blockchain.waves.model._

case class GenesisTransaction private(recipient: Address,
                                      timestamp: Timestamp,
                                      quantity: Long)
  extends Transaction
