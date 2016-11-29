package ru.tolsi.aobp.blockchain.waves.transaction.signer

import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves.{Signature64, WavesBlockChain, WavesSigner}


private[waves] class WavesTransactionSigner extends WavesSigner[WavesTransaction, WavesSignedTransaction[WavesTransaction], Signature64] {
  private def implicitlySign[TX <: WavesTransaction](tx: TX)(implicit bc: WavesBlockChain, signer: WavesSigner[TX, WavesSignedTransaction[TX], Signature64]): WavesSignedTransaction[TX] = {
    signer.sign(tx)
  }

  override def sign(tx: WavesTransaction)(implicit bc: WavesBlockChain): WavesSignedTransaction[WavesTransaction] = {
    tx match {
      case tx: GenesisTransaction => implicitlySign(tx)
      case tx: PaymentTransaction => implicitlySign(tx)
      case tx: IssueTransaction => implicitlySign(tx)
      case tx: ReissueTransaction => implicitlySign(tx)
      case tx: TransferTransaction => implicitlySign(tx)
    }
  }
}
