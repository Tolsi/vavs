package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.waves.transaction.serializer.bytes._

package object signer {
  private[block] implicit val wavesBlockSignCreator = new WavesBlockSignCreator(signedTransactionBytesSeqSerializer)
  implicit val wavesBlockSigner = new WavesBlockSigner
}
