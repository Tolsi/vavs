package ru.tolsi.aobp.blockchain.waves.storage.state

import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.storage.block.WavesBlockStorage
import ru.tolsi.aobp.blockchain.waves._

// todo separate later
private[waves] abstract class AbstractWavesStateStorage(blocksStorage: WavesBlockStorage) extends StateStorage[WavesBlockChain#SB[WavesBlock], BalanceAccount] with StateValidator {
  override type BalanceValue = Long
}
