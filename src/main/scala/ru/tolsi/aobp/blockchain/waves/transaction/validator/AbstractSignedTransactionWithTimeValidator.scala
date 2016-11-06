package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base.AbstractSignedTransactionValidator
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

// todo separate time and signature validation
private[validator] abstract class AbstractSignedTransactionWithTimeValidator[STX <: WavesBlockChain#ST[WavesBlockChain#T]](blockTimestamp: Long) extends AbstractSignedTransactionValidator[WavesBlockChain, WavesBlockChain#T, STX]
