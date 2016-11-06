package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.base.SignedBlockValidationError

class SignatureError(message: => String) extends SignedBlockValidationError(message)
