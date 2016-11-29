package ru.tolsi.aobp.blockchain.waves

import org.whispersystems.curve25519.Curve25519

abstract class WavesSigner[S <: Signable, SV <: Signed[S, SI], SI <: Signature[Array[Byte]]] extends Signer[S, SV, SI] {
  protected val curve25519 = Curve25519.getInstance(Curve25519.JAVA)
}
