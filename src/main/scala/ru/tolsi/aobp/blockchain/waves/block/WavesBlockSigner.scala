package ru.tolsi.aobp.blockchain.waves.block

import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesSigner}

class WavesBlockSigner(signCreator: ArraySignCreator[WavesBlockChain#B]) extends WavesSigner[WavesBlockChain#B, Signature64] {
  override def sign(b: WavesBlockChain#B)(implicit bc: WavesBlockChain): Signed[WavesBlockChain#B, Signature64] = {
    val sign = signCreator.createSign(b)
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(b.generator.privateKey.get, sign.value))
    SignedBlock(b, signature)
  }
}
