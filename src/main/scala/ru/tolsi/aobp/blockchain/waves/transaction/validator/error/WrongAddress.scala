package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}

class WrongAddress(message: => String) extends TransactionValidationError[WavesBlockChain, WavesBlockChain#T](message)
