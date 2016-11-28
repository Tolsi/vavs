package ru.tolsi.aobp.blockchain.waves.network.transport

import ru.tolsi.aobp.blockchain.waves.{NetworkLayer, ProtocolRequest, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.transaction.WavesTransaction
import rx.Observable

class WavesNetwork extends NetworkLayer {
  override def outgoingRequests: Observable[ProtocolRequest] = ???

  override def outgoingTx: Observable[WavesTransaction] = ???

  override def outgoingBlocks: Observable[WavesBlock] = ???

  override def incomingRequests: Observable[ProtocolRequest] = ???

  override def incomingTx: Observable[WavesTransaction] = ???

  override def incomingBlocks: Observable[WavesBlock] = ???
}
