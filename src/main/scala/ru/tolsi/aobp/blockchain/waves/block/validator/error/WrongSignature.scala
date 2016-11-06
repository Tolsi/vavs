package ru.tolsi.aobp.blockchain.waves.block.validator.error

import ru.tolsi.aobp.blockchain.base.BlockValidationError
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

class WrongSignature(message: => String) extends BlockValidationError[WavesBlockChain, WavesBlockChain#B](message)
