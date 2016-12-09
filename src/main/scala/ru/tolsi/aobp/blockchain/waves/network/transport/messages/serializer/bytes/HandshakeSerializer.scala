package ru.tolsi.aobp.blockchain.waves.network.transport.messages.serializer.bytes

import com.google.common.base.Charsets
import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer._
import ru.tolsi.aobp.blockchain.waves.network.transport.messages.Handshake

class HandshakeSerializer extends BytesSerializer[Handshake] {
  override def serialize(handshake: Handshake): Array[Byte] = {
    val applicationName = handshake.applicationName.getBytes(Charsets.UTF_8)

    val versionMajor = intBytesEnsureCapacity(handshake.applicationVersion.major)
    val versionMinor = intBytesEnsureCapacity(handshake.applicationVersion.minor)
    val versionPatch = intBytesEnsureCapacity(handshake.applicationVersion.patch)

    val nodeName = handshake.nodeName.getBytes(Charsets.UTF_8)
    val nodeNonce = longBytesEnsureCapacity(handshake.nonce)

    val declaredAddressBytesOpt = handshake.declaredAddress.map(address => address.getBytes(Charsets.UTF_8))
    val declaredAddressBytes = handshake.declaredAddress.map(address => address.getBytes(Charsets.UTF_8)).map(b => b.length.toByte +: b).getOrElse(Array[Byte](0))

    val timestamp = longBytesEnsureCapacity(handshake.timestamp)

    val packageLength = 30 + applicationName.length + nodeName.length + declaredAddressBytesOpt.map(_.length).getOrElse(0)
    val packageLengthBytes = intBytesEnsureCapacity(packageLength)

    Bytes.concat(
      packageLengthBytes,
      Array(applicationName.length.toByte),
      applicationName,
      versionMajor,
      versionMinor,
      versionPatch,
      Array(nodeName.length.toByte),
      nodeNonce,
      declaredAddressBytes,
      timestamp
    )
  }
}
