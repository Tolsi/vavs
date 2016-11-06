package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.base.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

class WrongAmount(message: => String) extends TransactionValidationError[WavesBlockChain, WavesBlockChain#T](message)
