package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

class WrongTimestamp[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)
