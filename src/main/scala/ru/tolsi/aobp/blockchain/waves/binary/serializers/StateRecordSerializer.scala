package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class StateRecordSerializer extends NullableSerializer[StateRecord] {

  override def write(kryo: Kryo, output: Output, record: StateRecord): Unit = {
    output.writeLong(record.balance)
    val len = record.changes.length
    output.writeInt(len)
    record.changes.foreach(change => kryo.writeObject(output, change))
    output.writeLong(record.prevHeight)
  }

  override def read(kryo: Kryo, input: Input, c: Class[StateRecord]): StateRecord = {
    val balance = input.readLong()
    val length = input.readInt()

    val changes: List[StateChange] = (1 to length).foldLeft(List[StateChange]()) { (result, i) =>
      val change = kryo.readObject(input, classOf[StateChange])
      result :+ change
    }

    val prevHeight = input.readLong()

    StateRecord(balance, changes, prevHeight)
  }
}
