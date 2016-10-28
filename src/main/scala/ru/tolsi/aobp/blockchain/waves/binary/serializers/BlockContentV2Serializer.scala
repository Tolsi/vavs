package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class BlockContentV2Serializer extends NullableSerializer[BlockContentV2] {
  override def write(kryo: Kryo, output: Output, content: BlockContentV2): Unit = {
    output.writeByte(content.version)
    output.writeLong(content.timestamp)
    kryo.writeObject(output, content.reference)
    kryo.writeObject(output, content.consensusData)
    kryo.writeObject(output, content.transactionsData)
  }

  override def read(kryo: Kryo, input: Input, c: Class[BlockContentV2]): BlockContentV2 = {
    val version = input.readByte()
    assert(version == BlockContentV2.Version, "Incorrect block version during deserialization")

    val timestamp = input.readLong()
    val reference = kryo.readObject(input, classOf[Signature64])
    val consensusData = kryo.readObject(input, classOf[ConsensusDataV1])
    val transactionsData = kryo.readObject(input, classOf[TransactionsDataV1])

    BlockContentV2(timestamp, reference, consensusData, transactionsData)
  }
}
