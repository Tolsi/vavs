package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction
import ru.tolsi.aobp.blockchain.waves.TransactionValidationError

class WrongState(message: => String) extends TransactionValidationError[WavesTransaction](message)
