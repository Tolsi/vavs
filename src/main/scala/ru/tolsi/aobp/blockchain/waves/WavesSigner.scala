package ru.tolsi.aobp.blockchain.waves

import com.google.common.primitives.Shorts
import ru.tolsi.aobp.blockchain.base._

abstract class WavesSigner[S <: Signable with WithSign, SV <: Signed[S, SI], SI <: Signature[Array[Byte]]] extends Signer[WavesBlockChain, S, SV, SI]
