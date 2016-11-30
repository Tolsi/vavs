package ru.tolsi.aobp.blockchain.waves.serializer

import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.Address

// todo is usefull?
class AddressSerializer extends BytesSerializer[Address] {
  override def serialize(obj: Address): Array[Byte] = obj.address
}
