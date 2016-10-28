package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class BlockCheckpointSetSerializer extends NullableSerializer[BlockCheckpointSet] {
  override def write(kryo: Kryo, output: Output, set: BlockCheckpointSet): Unit = {
    output.writeInt(set.checkpoints.length)
    set.checkpoints.foreach(checkpoint => kryo.writeObject(output, checkpoint, new BlockCheckpointSerializer))

  }

  override def read(kryo: Kryo, input: Input, c: Class[BlockCheckpointSet]): BlockCheckpointSet = {
    val length = input.readInt()
    val checkpoints: Seq[BlockCheckpoint] = (1 to length).foldLeft(Seq[BlockCheckpoint]()) { (result, i) =>
      val checkpoint = kryo.readObject(input, classOf[BlockCheckpoint], new BlockCheckpointSerializer)
      result :+ checkpoint
    }

    BlockCheckpointSet(checkpoints)
  }
}
