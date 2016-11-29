package ru.tolsi.aobp.blockchain.waves.block.signer

import ru.tolsi.aobp.blockchain.waves.block.{WavesSignedBlock, WavesBlock}
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class WavesBlockSigner(implicit signCreator: DataForSignCreator[WavesBlock]) extends WavesSigner[WavesBlock, WavesSignedBlock[WavesBlock], Signature64] {
  override def sign(b: WavesBlock)(implicit bc: WavesBlockChain): WavesSignedBlock[WavesBlock] = {
    val data = signCreator.createDataForSign(b)
    val signature = new Signature64(curve25519.calculateSignature(b.generator.privateKey.get, data.value))
    WavesSignedBlock(b, signature)
  }
}
