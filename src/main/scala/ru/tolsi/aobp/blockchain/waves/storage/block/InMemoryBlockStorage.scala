package ru.tolsi.aobp.blockchain.waves.storage.block

import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.storage.NotThreadSafeStorage
import ru.tolsi.aobp.blockchain.waves.{BlockStorage, SB}

import scala.collection.mutable

abstract class WavesBlockStorage extends BlockStorage[SB[WavesBlock], WavesBlock#Id]

class InMemoryBlockStorage extends WavesBlockStorage with NotThreadSafeStorage {
  private val map = new mutable.AnyRefMap[BlockId, SignedBlock]()

  override def put(block: SignedBlock): Unit = map += (block.signature -> block)

  override def get(id: BlockId): Option[SignedBlock] = map.get(id)

  override def contains(id: BlockId): Boolean = map.contains(id)

  override def remove(id: BlockId): Option[SignedBlock] = map.remove(id)
}
