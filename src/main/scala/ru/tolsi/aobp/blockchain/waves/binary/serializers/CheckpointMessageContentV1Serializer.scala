package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class CheckpointMessageContentV1Serializer extends NullableSerializer[CheckpointMessageContentV1] {
  override def write(kryo: Kryo, output: Output, content: CheckpointMessageContentV1): Unit = {
    kryo.writeObject(output, content.checkpoint.checkpoints, new BlockCheckpointSetSerializer)
    kryo.writeObject(output, content.checkpoint.signature, new Signature64Serializer)
  }

  override def read(kryo: Kryo, input: Input, c: Class[CheckpointMessageContentV1]): CheckpointMessageContentV1 = {
    val checkpoints = kryo.readObject(input, classOf[BlockCheckpointSet], new BlockCheckpointSetSerializer)
    val signature = kryo.readObject(input, classOf[Signature64], new Signature64Serializer)

    CheckpointMessageContentV1(SignedCheckpoint(checkpoints, signature))
  }
}
