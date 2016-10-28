package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class Signature32Serializer extends NullableSerializer[Signature32]{
  override def write(kryo: Kryo, output: Output, signature: Signature32): Unit = {
    output.writeBytes(signature.bytes)
  }

  override def read(kryo: Kryo, input: Input, c: Class[Signature32]): Signature32 = {
    val bytes = input.readBytes(Signature32.Length)

    Signature32(bytes)
  }
}
