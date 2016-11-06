package ru.tolsi.aobp.blockchain.waves.block

package object validator {
  implicit val genesisBlockValidator = new GenesisBlockValidator
  implicit val baseBlockValidator = new BaseBlockValidator
  implicit val unsignedBlockValidator = new UnsignedBlockValidator
  implicit val signedBlockValidator = new SignedBlockValidator
}
