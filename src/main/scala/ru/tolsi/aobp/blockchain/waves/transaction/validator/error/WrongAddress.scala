package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.base.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

class WrongAddress(message: => String) extends TransactionValidationError[WavesBlockChain, WavesBlockChain#T](message)
