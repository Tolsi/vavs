package ru.tolsi.aobp.blockchain.waves.serializer

import com.google.common.primitives.Shorts

object BytesUtils {
  def arrayWithSize(b: Array[Byte]): Array[Byte] = Shorts.toByteArray(b.length.toShort) ++ b

  def booleanToByte(b: Boolean): Byte = (if (b) 1 else 0).toByte

  def optionByteArrayToByteArray(a: Option[Array[Byte]]): Array[Byte] = a.map(a => (1: Byte) +: a).getOrElse(Array(0: Byte))
}
