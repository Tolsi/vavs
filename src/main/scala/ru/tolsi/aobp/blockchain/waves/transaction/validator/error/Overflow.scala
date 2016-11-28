package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction
import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}

class Overflow(message: => String) extends TransactionValidationError[WavesTransaction](message)
