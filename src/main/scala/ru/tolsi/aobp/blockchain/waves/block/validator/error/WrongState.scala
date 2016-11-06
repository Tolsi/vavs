package ru.tolsi.aobp.blockchain.waves.block.validator.error

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

class WrongState(message: => String) extends BlockValidationError[WavesBlockChain, WavesBlockChain#B](message)
