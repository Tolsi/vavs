package ru.tolsi.aobp.blockchain.waves.storage.block

import ru.tolsi.aobp.blockchain.base.BlockStorage
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.storage.NotThreadSafeStorage

import scala.collection.mutable

abstract class WavesBlockStorage[W <: WavesBlockChain] extends BlockStorage[W, W#SB[W#B], W#B#Id]

class InMemoryBlockStorage[W <: WavesBlockChain] extends WavesBlockStorage[W] with NotThreadSafeStorage {
  private val map = new mutable.AnyRefMap[BlockId, SignedBlock]()

  override def put(block: SignedBlock): Unit = map += (block.signature -> block)

  override def get(id: BlockId): Option[SignedBlock] = map.get(id)

  override def contains(id: BlockId): Boolean = map.contains(id)

  override def remove(id: BlockId): Option[SignedBlock] = map.remove(id)
}
