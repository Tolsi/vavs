package ru.tolsi.aobp.blockchain.waves.block.signer

import org.whispersystems.curve25519.Curve25519
import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.block.SignedBlock
import ru.tolsi.aobp.blockchain.waves.{SignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class WavesBlockSigner(implicit signCreator: SignCreator[WavesBlockChain#B]) extends WavesSigner[WavesBlockChain#B, SignedBlock[WavesBlockChain#B], Signature64] {
  override def sign(b: WavesBlockChain#B)(implicit bc: WavesBlockChain): SignedBlock[WavesBlockChain#B] = {
    val sign = signCreator.createSign(b)
    val signature = new Signature64(Curve25519.getInstance(Curve25519.JAVA).calculateSignature(b.generator.privateKey.get, sign.value))
    SignedBlock(b, signature)
  }
}
