package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class GetBlockMessageContentV1Serializer extends NullableSerializer[GetBlockMessageContentV1] {
  override def write(kryo: Kryo, output: Output, content: GetBlockMessageContentV1): Unit = {
    kryo.writeObject(output, content.blockId)
  }

  override def read(kryo: Kryo, input: Input, c: Class[GetBlockMessageContentV1]): GetBlockMessageContentV1 = {
    val blockId = kryo.readObject(input, classOf[Signature64])

    GetBlockMessageContentV1(blockId)
  }
}
