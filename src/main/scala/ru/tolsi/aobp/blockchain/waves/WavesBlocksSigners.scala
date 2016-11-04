package ru.tolsi.aobp.blockchain.waves

import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base._

private[waves] trait WavesBlocksSigners {
  this: WavesBlockChain =>

  implicit object BlockSigner extends WavesSigner[B, Signature64] {
    val signCreator = implicitly[ArraySignCreator[WavesBlock]]
    override def sign(b: WavesBlock): Signed[WavesBlock, Signature64] = {
      val sign = signCreator.createSign(b)
      val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(b.generator.privateKey.get, sign.value))
      SignedBlock(b, signature)
    }
  }
}
