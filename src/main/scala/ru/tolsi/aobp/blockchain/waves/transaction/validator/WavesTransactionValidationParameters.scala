package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base.BlockTransactionParameters
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

case class WavesTransactionValidationParameters(blockTimestamp: Long) extends BlockTransactionParameters[WavesBlockChain]
