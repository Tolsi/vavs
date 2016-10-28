package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class TransactionMessageContentV1Serializer extends NullableSerializer[TransactionMessageContentV1] {
  override def write(kryo: Kryo, output: Output, content: TransactionMessageContentV1): Unit = {
    kryo.writeObject(output, content.signedTransaction)
  }

  override def read(kryo: Kryo, input: Input, c: Class[TransactionMessageContentV1]): TransactionMessageContentV1 = {
    val transaction = kryo.readObject(input, classOf[SignedTransaction[_ <: Transaction]])

    TransactionMessageContentV1(transaction)
  }
}
