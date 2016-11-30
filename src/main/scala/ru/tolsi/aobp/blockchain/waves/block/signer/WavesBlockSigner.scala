package ru.tolsi.aobp.blockchain.waves.block.signer

import ru.tolsi.aobp.blockchain.waves.block.{WavesSignedBlock, WavesBlock}
import ru.tolsi.aobp.blockchain.waves.{DataForSignCreator, Signature64, WavesBlockChain, WavesSigner}

private[signer] class WavesBlockSigner(implicit dataForSignCreator: DataForSignCreator[WavesBlock]) extends WavesSigner[WavesBlock, WavesSignedBlock[WavesBlock], Signature64] {
  override def sign(b: WavesBlock)(implicit bc: WavesBlockChain): WavesSignedBlock[WavesBlock] = {
    val data = dataForSignCreator.serialize(b)
    val signature = new Signature64(curve25519.calculateSignature(b.generator.privateKey.get, data))
    WavesSignedBlock(b, signature)
  }
}
