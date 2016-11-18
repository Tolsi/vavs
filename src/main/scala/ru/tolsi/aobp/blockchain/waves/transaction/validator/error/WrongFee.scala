package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}

class WrongFee(message: => String) extends TransactionValidationError[WavesBlockChain, WavesBlockChain#T](message)
