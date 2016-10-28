package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class BlockContentV1Serializer extends NullableSerializer[BlockContentV1] {
  override def write(kryo: Kryo, output: Output, content: BlockContentV1): Unit = {
    output.writeByte(content.version)
    output.writeLong(content.timestamp)
    kryo.writeObject(output, content.reference)
    kryo.writeObject(output, content.consensusData, new ConsensusDataV1Serializer())
    kryo.writeObject(output, content.transactionsData, new TransactionsDataV1Serializer())
  }

  override def read(kryo: Kryo, input: Input, c: Class[BlockContentV1]): BlockContentV1 = {
    val version = input.readByte()
    assert(version == BlockContentV1.Version, "Incorrect block version during deserialization")

    val timestamp = input.readLong()
    val reference = kryo.readObject(input, classOf[Signature64])
    val consensusData = kryo.readObject(input, classOf[ConsensusDataV1])
    val transactionsData = kryo.readObject(input, classOf[TransactionsDataV1])

    BlockContentV1(timestamp, reference, consensusData, transactionsData)
  }
}
