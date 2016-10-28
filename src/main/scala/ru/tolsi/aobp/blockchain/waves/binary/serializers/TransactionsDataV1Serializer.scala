package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class TransactionsDataV1Serializer extends NullableSerializer[TransactionsDataV1] {
  override def write(kryo: Kryo, output: Output, data: TransactionsDataV1): Unit = {
    output.writeInt(data.transactions.length)
    data.transactions.foreach(kryo.writeObject(output, _))
  }

  override def read(kryo: Kryo, input: Input, c: Class[TransactionsDataV1]): TransactionsDataV1 = {
    val length = input.readInt()
    val transactions: Seq[SignedTransaction[_ <: Transaction]] =
      (1 to length).foldLeft(Seq[SignedTransaction[_ <: Transaction]]()) { (result, i) =>
        val transaction = kryo.readObject(input, classOf[SignedTransaction[_ <: Transaction]])
        result :+ transaction
      }

    TransactionsDataV1(transactions)
  }
}
