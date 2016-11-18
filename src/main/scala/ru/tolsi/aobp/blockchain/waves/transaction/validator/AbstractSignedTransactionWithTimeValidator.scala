package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.{AbstractSignedTransactionValidator, WavesBlockChain}

// todo separate time and signature validation
private[validator] abstract class AbstractSignedTransactionWithTimeValidator[STX <: WavesBlockChain#ST[WavesBlockChain#T]](blockTimestamp: Long) extends AbstractSignedTransactionValidator[WavesBlockChain, WavesBlockChain#T, STX]
