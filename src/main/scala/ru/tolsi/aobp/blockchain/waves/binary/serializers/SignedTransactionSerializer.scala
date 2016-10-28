package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class SignedTransactionSerializer extends NullableSerializer[SignedTransaction[_ <: Transaction]] {
  override def write(kryo: Kryo, output: Output, transaction: SignedTransaction[_ <: Transaction]): Unit = {
    kryo.writeObject(output, transaction.transaction)
    kryo.writeObject(output, transaction.signature)
  }

  override def read(kryo: Kryo, input: Input, c: Class[SignedTransaction[_ <: Transaction]]):
  SignedTransaction[_ <: Transaction] = {
    val pos = input.position()
    val transactionType = input.readByte()
    input.setPosition(pos)
    val transaction = transactionType match {
      case GenesisTransactionV1.TransactionTypeId => kryo.readObject(input, classOf[GenesisTransactionV1])
      case PaymentTransactionV1.TransactionTypeId => kryo.readObject(input, classOf[PaymentTransactionV1])
      case _ => throw new AssertionError("Unsupported transaction type")
    }
    val signature = kryo.readObject(input, classOf[Signature64])

    SignedTransaction(transaction, signature)
  }
}
