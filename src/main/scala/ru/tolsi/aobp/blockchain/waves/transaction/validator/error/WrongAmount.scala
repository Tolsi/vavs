package ru.tolsi.aobp.blockchain.waves.transaction.validator.error

import ru.tolsi.aobp.blockchain.base.TransactionValidationError
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

class WrongAmount[+TX <: WavesBlockChain#T](message: => String) extends TransactionValidationError[WavesBlockChain, TX](message)
