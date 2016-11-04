package ru.tolsi.aobp.blockchain.waves

import ru.tolsi.aobp.blockchain.base.{Signature32, Signature64, Signed}

private[waves] trait WavesBlocks {
  this: WavesBlockChain =>

  abstract sealed class WavesBlock extends BlockChainBlock {
    override type Id = Signature64

    def version: Byte

    def timestamp: Long

    def reference: Signature64

    def baseTarget: Long

    // fasthash(lastBlockData.generationSignature ++ generator.publicKey)
    def generationSignature: Signature32

    def fee: WavesMoney[Either[Waves.type, Asset]]

    def transactions: Seq[Signed[WavesTransaction, Signature64]]

    def generator: Account
  }

  case class SignedBlock[BL <: B](block: BL, signature: Signature64) extends WavesBlock with BlockChainSignedBlock[BL, Signature64] {
    override def version: Byte = block.version

    override def timestamp: Long = block.timestamp

    override def reference: Signature64 = block.reference

    override def transactions: Seq[Signed[WavesTransaction, Signature64]] = block.transactions

    override def baseTarget: Long = block.baseTarget

    override def generator: Account = block.generator

    override def generationSignature: Signature32 = block.generationSignature

    override def signed: BL = block

    override def fee: WavesMoney[Either[Waves.type, Asset]] = block.fee
  }

  case class GenesisBlock(timestamp: Long,
                          reference: Signature64,
                          baseTarget: Long,
                          generator: Account,
                          generationSignature: Signature32,
                          fee: WavesMoney[Either[Waves.type, Asset]],
                          transactions: Seq[Signed[WavesTransaction, Signature64]]
                         ) extends WavesBlock {
    val version: Byte = 2
  }


  case class BaseBlock(timestamp: Long,
                       reference: Signature64,
                       baseTarget: Long,
                       generator: Account,
                       generationSignature: Signature32,
                       fee: WavesMoney[Either[Waves.type, Asset]],
                       transactions: Seq[Signed[WavesTransaction, Signature64]]) extends WavesBlock {
    val version: Byte = 3
  }

}
