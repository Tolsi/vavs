package ru.tolsi.aobp.blockchain.waves.storage.block

import ru.tolsi.aobp.blockchain.base.BlockStorage
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain

import scala.collection.mutable

class InMemoryBlockStorage extends BlockStorage[WavesBlockChain] {
  private val map = new mutable.AnyRefMap[BlockId, SignedBlock]()

  override def put(block: SignedBlock): Unit = map += (block.signature -> block)

  override def get(id: BlockId): Option[SignedBlock] = map.get(id)

  override def contains(id: BlockId): Boolean = map.contains(id)

  override def remove(id: BlockId): Option[SignedBlock] = map.remove(id)
}
