package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class Signature64Serializer extends NullableSerializer[Signature64] {
  override def write(kryo: Kryo, output: Output, signature: Signature64): Unit = {
    output.writeBytes(signature.bytes)
  }

  override def read(kryo: Kryo, input: Input, c: Class[Signature64]): Signature64 = {
    val bytes = input.readBytes(Signature64.Length)

    Signature64(bytes)
  }
}
