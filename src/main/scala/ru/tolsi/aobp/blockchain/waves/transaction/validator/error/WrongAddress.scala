package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.waves.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction

class WrongAddress(message: => String) extends TransactionValidationError[WavesTransaction](message)
