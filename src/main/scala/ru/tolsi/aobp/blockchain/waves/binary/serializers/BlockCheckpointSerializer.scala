package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class BlockCheckpointSerializer extends NullableSerializer[BlockCheckpoint] {
  override def write(kryo: Kryo, output: Output, checkpoint: BlockCheckpoint): Unit = {
    output.writeLong(checkpoint.height)
    kryo.writeObject(output, checkpoint.signature, new Signature64Serializer)
  }

  override def read(kryo: Kryo, input: Input, c: Class[BlockCheckpoint]): BlockCheckpoint = {
    val height = input.readLong()
    val signature = kryo.readObject(input, classOf[Signature64], new Signature64Serializer)

    BlockCheckpoint(height, signature)
  }
}
