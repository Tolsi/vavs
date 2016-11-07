package ru.tolsi.aobp.blockchain.waves.transaction.signer

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves.{WavesBlockChain, WavesSigner}


private[waves] class WavesTransactionSigner extends WavesSigner[WavesBlockChain#T, SignedTransaction[WavesBlockChain#T], Signature64] {
  private def implicitlySign[TX <: WavesBlockChain#T](tx: TX)(implicit bc: WavesBlockChain, signer: WavesSigner[TX, SignedTransaction[TX], Signature64]): SignedTransaction[TX] = {
    signer.sign(tx)
  }

  override def sign(tx: WavesTransaction)(implicit bc: WavesBlockChain): SignedTransaction[WavesTransaction] = {
    tx match {
      case tx: GenesisTransaction => implicitlySign(tx)
      case tx: PaymentTransaction => implicitlySign(tx)
      case tx: IssueTransaction => implicitlySign(tx)
      case tx: ReissueTransaction => implicitlySign(tx)
      case tx: TransferTransaction => implicitlySign(tx)
    }
  }
}
