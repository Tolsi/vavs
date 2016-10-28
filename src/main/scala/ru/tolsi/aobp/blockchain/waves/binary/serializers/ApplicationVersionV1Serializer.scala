package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class ApplicationVersionV1Serializer extends NullableSerializer[ApplicationVersionV1] {
  override def write(kryo: Kryo, output: Output, version: ApplicationVersionV1): Unit = {
    output.writeInt(version.major)
    output.writeInt(version.minor)
    output.writeInt(version.patch)
  }

  override def read(kryo: Kryo, input: Input, c: Class[ApplicationVersionV1]): ApplicationVersionV1 = {
    val major = input.readInt()
    val minor = input.readInt()
    val patch = input.readInt()

    ApplicationVersionV1(major, minor, patch)
  }
}
