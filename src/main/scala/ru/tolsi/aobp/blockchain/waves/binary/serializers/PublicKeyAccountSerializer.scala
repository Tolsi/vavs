package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import scorex.crypto.signatures.Curve25519

class PublicKeyAccountSerializer extends NullableSerializer[PublicKeyAccount] {
  override def write(kryo: Kryo, output: Output, key: PublicKeyAccount): Unit = {
    output.writeBytes(key.publicKey)
  }

  override def read(kryo: Kryo, input: Input, c: Class[PublicKeyAccount]): PublicKeyAccount = {
    val bytes = input.readBytes(Curve25519.KeyLength)

    new PublicKeyAccount(bytes)
  }
}
