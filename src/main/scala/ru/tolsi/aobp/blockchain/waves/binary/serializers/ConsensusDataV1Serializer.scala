package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class ConsensusDataV1Serializer extends NullableSerializer[ConsensusDataV1] {
  override def write(kryo: Kryo, output: Output, data: ConsensusDataV1): Unit = {
    output.writeLong(data.baseTarget)
    kryo.writeObject(output, data.generatorSignature)
  }

  override def read(kryo: Kryo, input: Input, c: Class[ConsensusDataV1]): ConsensusDataV1 = {
    val target = input.readLong()
    val signature = kryo.readObject(input, classOf[Signature32])

    ConsensusDataV1(target, signature)
  }
}
