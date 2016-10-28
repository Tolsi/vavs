package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class SignaturesMessageContentV1Serializer extends NullableSerializer[SignaturesMessageContentV1] {

  override def write(kryo: Kryo, output: Output, content: SignaturesMessageContentV1): Unit = {
    output.writeInt(content.signatures.length)
    content.signatures.foreach(s => kryo.writeObject(output, s))
  }

  override def read(kryo: Kryo, input: Input, c: Class[SignaturesMessageContentV1]): SignaturesMessageContentV1 = {
    val lenght = input.readInt()
    val signatures = (1 to lenght).foldLeft(Seq[Signature64]()) { (result, i) =>
      val signature = kryo.readObject(input, classOf[Signature64])
      result :+ signature
    }

    SignaturesMessageContentV1(signatures)
  }
}
