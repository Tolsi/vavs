package ru.tolsi.aobp.blockchain.waves.binary

import com.twitter.chill.{AllScalaRegistrar, KryoBase, KryoPool, ScalaKryoInstantiator}

class WavesKryoInstantiator extends ScalaKryoInstantiator {
  override def newKryo(): KryoBase = {
    val kryo = super.newKryo()
    val allScalaRegistrar = new AllScalaRegistrar()
    val registrar = new WavesKryoRegistrar()
    allScalaRegistrar(kryo)
    registrar(kryo)

    kryo
  }
}

object WavesKryoInstantiator {
  lazy val defaultPool: KryoPool = KryoPool.withByteArrayOutputStream(guessThreads, new WavesKryoInstantiator)

  private def guessThreads: Int = {
    val cores = Runtime.getRuntime.availableProcessors
    val GUESS_THREADS_PER_CORE = 4
    GUESS_THREADS_PER_CORE * cores
  }
}
