package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.waves.transaction.validator.signedTransactionWithTimeValidator
import ru.tolsi.aobp.blockchain.waves.block.signer.wavesBlockSigner

package object validator {
  implicit val genesisBlockValidator = new GenesisBlockValidator
  implicit val baseBlockValidator = new BaseBlockValidator
  implicit val unsignedBlockValidator = new UnsignedBlockValidator(signedTransactionWithTimeValidator)
  implicit val signedBlockValidator = new SignedBlockValidator
}
