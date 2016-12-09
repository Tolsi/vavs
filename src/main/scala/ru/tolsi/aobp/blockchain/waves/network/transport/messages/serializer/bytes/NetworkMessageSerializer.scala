package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.network.transport.NetworkMessage
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.NetworkMessageChecksum

abstract class NetworkMessageSerializer[NM <: NetworkMessage] extends BytesSerializer[NM] with NetworkMessageChecksum
