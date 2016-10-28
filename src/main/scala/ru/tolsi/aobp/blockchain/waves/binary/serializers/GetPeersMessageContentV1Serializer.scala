package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class GetPeersMessageContentV1Serializer extends NullableSerializer[GetPeersMessageContentV1] {
  override def write(kryo: Kryo, output: Output, content: GetPeersMessageContentV1): Unit = {
  }

  override def read(kryo: Kryo, input: Input, c: Class[GetPeersMessageContentV1]): GetPeersMessageContentV1 = {
    GetPeersMessageContentV1()
  }
}
