package ru.tolsi.aobp.blockchain.waves.block

import ru.tolsi.aobp.blockchain.waves.transaction.validator.signedTransactionWithTimeValidator
import ru.tolsi.aobp.blockchain.waves.block.signer.wavesBlockSigner

package object validator {
  private[validator] implicit val genesisBlockValidator = new GenesisBlockValidator
  private[validator] implicit val baseBlockValidator = new BaseBlockValidator
  val unsignedBlockValidator = new UnsignedBlockValidator(signedTransactionWithTimeValidator)
  val signedBlockValidator = new SignedBlockValidator(unsignedBlockValidator, wavesBlockSigner)
}
