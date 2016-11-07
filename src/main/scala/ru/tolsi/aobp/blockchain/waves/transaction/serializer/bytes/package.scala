package ru.tolsi.aobp.blockchain.waves.transaction.serializer

import ru.tolsi.aobp.blockchain.waves.transaction.signcreator.wavesTransactionSignCreator

package object bytes {
  implicit val signedTransactionBytesSerializer = new SignedTransactionBytesSerializer
}
