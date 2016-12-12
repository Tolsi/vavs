package ru.tolsi.aobp.blockchain.waves.model.transaction

import ru.tolsi.aobp.blockchain.waves.model.Currency._
import ru.tolsi.aobp.blockchain.waves.model._
case class TransferTransaction(timestamp: Timestamp,
                               sender: Address,
                               recipient: Address,
                               quantity: Volume,
                               fee: Volume,
                               attachment: Array[Byte])
    extends FromToTransaction
