package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class SignedBlockSerializer extends NullableSerializer[SignedBlock[_ <: BlockContent]] {
  override def write(kryo: Kryo, output: Output, block: SignedBlock[_ <: BlockContent]): Unit = {
    kryo.writeObject(output, block.content)
    kryo.writeObject(output, block.credentials)
  }

  override def read(kryo: Kryo, input: Input, c: Class[SignedBlock[_ <: BlockContent]]): SignedBlock[_ <: BlockContent] = {
    val content = input.getBuffer.head match {
      case BlockContentV1.Version => kryo.readObject(input, classOf[BlockContentV1])
      case BlockContentV2.Version => kryo.readObject(input, classOf[BlockContentV2])
      case _ => throw new AssertionError("Unsupported block content type")
    }
    val credentials = kryo.readObject(input, classOf[Credentials])

    SignedBlock(content, credentials)
  }
}
