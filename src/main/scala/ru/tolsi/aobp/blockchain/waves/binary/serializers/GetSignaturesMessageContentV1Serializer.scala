package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class GetSignaturesMessageContentV1Serializer extends NullableSerializer[GetSignaturesMessageContentV1] {

  override def write(kryo: Kryo, output: Output, content: GetSignaturesMessageContentV1): Unit = {
    output.writeInt(content.blockIds.length)
    content.blockIds.foreach(id => kryo.writeObject(output, id))
  }

  override def read(kryo: Kryo, input: Input, c: Class[GetSignaturesMessageContentV1]): GetSignaturesMessageContentV1 = {
    val length = input.readInt()
    val ids: Seq[Signature64] = (1 to length).foldLeft(Seq[Signature64]()) { (result, i) =>
      val id = kryo.readObject(input, classOf[Signature64])
      result :+ id
    }

    GetSignaturesMessageContentV1(ids)
  }
}
