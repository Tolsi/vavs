package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.waves.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction

class WrongAssetName(message: => String) extends TransactionValidationError[WavesTransaction](message)
