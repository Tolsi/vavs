package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

class WrongTimestamp(message: => String) extends TransactionValidationError[WavesBlockChain, WavesBlockChain#T](message)
