package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.waves.transaction.signer.wavesTransactionSigner
import ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes.signedTransactionBytesSerializer

package object signer {
  private[block] implicit val wavesBlockSignCreator = new WavesBlockSignCreator
  implicit val wavesBlockSigner = new WavesBlockSigner
}
