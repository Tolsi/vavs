package ru.tolsi.aobp.blockchain.waves.storage.state

import ru.tolsi.aobp.blockchain.base.StateValidator
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage
import ru.tolsi.aobp.blockchain.waves.{StateStorage, StateValidator, WavesBlockChain, WavesСurrency}

// todo separate later
private[waves] abstract class AbstractWavesStateStorage(blocksStorage: WavesBlockStorage) extends StateStorage[WavesBlockChain, WavesBlockChain#SB[WavesBlockChain#B], WavesBlockChain#BA] with StateValidator[WavesBlockChain, WavesBlockChain#B, WavesBlockChain#T] {
  override type BalanceValue = Long
}
