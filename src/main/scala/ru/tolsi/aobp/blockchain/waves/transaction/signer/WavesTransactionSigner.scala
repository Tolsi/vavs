package ru.tolsi.aobp.blockchain.waves.transaction.signer

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction._


private[waves] class WavesTransactionSigner extends WavesSigner[WavesBlockChain#T, Signature64] {
  private def implicitlySign[TX <: WavesBlockChain#T](tx: TX)(implicit signer: WavesSigner[TX, Signature64]): Signed[TX, Signature64] = {
    signer.sign(tx)
  }

  override def sign(tx: WavesTransaction)(implicit bc: WavesBlockChain): Signed[WavesTransaction, Signature64] = {
    tx match {
      case tx: GenesisTransaction => implicitlySign(tx)
      case tx: PaymentTransaction => implicitlySign(tx)
      case tx: IssueTransaction => implicitlySign(tx)
      case tx: ReissueTransaction => implicitlySign(tx)
      case tx: TransferTransaction => implicitlySign(tx)
    }
  }
}
