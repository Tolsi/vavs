package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class PaymentTransactionV1Serializer extends NullableSerializer[PaymentTransactionV1] {
  override def write(kryo: Kryo, output: Output, transaction: PaymentTransactionV1): Unit = {
    output.writeByte(PaymentTransactionV1.TransactionTypeId)
    output.writeLong(transaction.timestamp)
    kryo.writeObject(output, transaction.sender, new PublicKeyAccountSerializer)
    kryo.writeObject(output, transaction.recipient, new AccountSerializer)
    output.writeLong(transaction.amount)
    output.writeLong(transaction.fee)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[PaymentTransactionV1]): PaymentTransactionV1 = {
    val transactionType = input.readByte()
    assert(transactionType == PaymentTransactionV1.TransactionTypeId, "Incorrect transaction type during deserialization")

    val timestamp = input.readLong()
    val sender = kryo.readObject(input, classOf[PublicKeyAccount])
    val receiver = kryo.readObject(input, classOf[Account])
    val amount = input.readLong()
    val fee = input.readLong()

    PaymentTransactionV1(sender, receiver, timestamp, amount, fee)
  }
}
