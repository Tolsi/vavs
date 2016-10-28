package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

class CredentialsSerializer extends NullableSerializer[Credentials] {
  override def write(kryo: Kryo, output: Output, credentials: Credentials): Unit = {
    kryo.writeObject(output, credentials.generator)
    kryo.writeObject(output, credentials.signature)
  }

  override def read(kryo: Kryo, input: Input, c: Class[Credentials]): Credentials = {
    val publicKeyAccount = kryo.readObject(input, classOf[PublicKeyAccount])
    val signature = kryo.readObject(input, classOf[Signature64])

    Credentials(publicKeyAccount, signature)
  }
}
