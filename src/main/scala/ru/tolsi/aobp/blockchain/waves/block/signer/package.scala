package ru.tolsi.aobp.blockchain.waves.block

package object signer {
  private[block] implicit val wavesBlockSignCreator = new WavesBlockSignCreator
  implicit val wavesBlockSigner = new WavesBlockSigner
}
