package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.{BlockTransactionParameters, WavesBlockChain}

case class WavesTransactionValidationParameters(blockTimestamp: Long) extends BlockTransactionParameters[WavesBlockChain]
