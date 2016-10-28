package ru.tolsi.aobp.blockchain.waves.storage

import java.nio.ByteBuffer

import org.h2.mvstore.WriteBuffer
import org.h2.mvstore.`type`.DataType

import scala.reflect.ClassTag


trait KryoDataType[T <: AnyRef] extends DataType  {
  private val pool = WavesKryoInstantiator.defaultPool

  // todo it works?
  implicit protected def tag: ClassTag[T] = reflect.classTag[T]

  private def serialize(t: Any): Array[Byte] = pool.toBytesWithoutClass(t)

  override def write(buff: WriteBuffer, obj: scala.Any): Unit = {
    val bytes = serialize(obj)
    buff.putInt(bytes.length)
    buff.put(bytes)
  }

  override def write(buff: WriteBuffer, objs: Array[AnyRef], len: Int, key: Boolean): Unit = {
    objs.foreach(serialize)
  }

  override def read(buff: ByteBuffer, result: Array[AnyRef], len: Int, key: Boolean): Unit = {
    for {i <- 0 until len} result(i) = read(buff)
  }

  override def read(buff: ByteBuffer): T = {
    val lenght = buff.getInt
    val newPosition = buff.position() + lenght
    val bytes = buff.get(Array.fill(lenght)(0.toByte), buff.position(), lenght).array()
    buff.position(newPosition)
    pool.fromBytes(bytes, tag.runtimeClass).asInstanceOf[T]
  }

  override def getMemory(obj: scala.Any): Int = serialize(obj).length
}
