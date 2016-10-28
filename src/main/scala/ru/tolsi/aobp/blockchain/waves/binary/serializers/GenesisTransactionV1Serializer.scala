package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class GenesisTransactionV1Serializer extends NullableSerializer[GenesisTransactionV1] {
  override def write(kryo: Kryo, output: Output, transaction: GenesisTransactionV1): Unit = {
    output.writeByte(GenesisTransactionV1.TransactionTypeId)
    output.writeLong(transaction.timestamp)
    kryo.writeObject(output, transaction.recipient, new AccountSerializer)
    output.writeLong(transaction.amount)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[GenesisTransactionV1]): GenesisTransactionV1 = {
    val transactionType = input.readByte()
    assert(transactionType == GenesisTransactionV1.TransactionTypeId, "Incorrect transaction type during deserialization")

    val timestamp = input.readLong()
    val account = kryo.readObject(input, classOf[Account])
    val amount = input.readLong()

    GenesisTransactionV1(account, timestamp, amount)
  }
}
