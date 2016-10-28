package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class StateChangeSerializer extends NullableSerializer[StateChange] {
  override def write(kryo: Kryo, output: Output, change: StateChange): Unit = {
    output.writeLong(change.amount)
    val referenceDefined = change.reference.isDefined
    output.writeBoolean(referenceDefined)
    if (referenceDefined) kryo.writeObject(output, change.reference.get)
  }

  override def read(kryo: Kryo, input: Input, c: Class[StateChange]): StateChange = {
    val amount = input.readLong()
    val referenceDefined = input.readBoolean()
    val reference = if (referenceDefined)
      Some(kryo.readObject(input, classOf[Signature64]))
    else
      None

    StateChange(amount, reference)
  }
}
