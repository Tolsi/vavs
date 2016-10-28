package ru.tolsi.aobp.blockchain.waves.binary.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import ru.tolsi.aobp.blockchain.waves.WavesPublicKeyAccount$
import scorex.crypto.encode.Base58

class AccountSerializer extends NullableSerializer[WavesPublicKeyAccount] {
  override def write(kryo: Kryo, output: Output, key: WavesPublicKeyAccount): Unit = {
    output.writeBytes(Base58.decode(key.address).get)
  }

  override def read(kryo: Kryo, input: Input, c: Class[WavesPublicKeyAccount]): WavesPublicKeyAccount = {
    val bytes = input.readBytes(WavesPublicKeyAccount.AddressLength)

    new WavesPublicKeyAccount(Base58.encode(bytes))
  }
}

