package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction._


private[waves] class WavesTransactionSignCreator extends SignCreator[WavesBlockChain#T] {
  private def implicitlyCreateSign[TX <: WavesBlockChain#T](tx: TX)(implicit signCreator: SignCreator[TX]): Sign[TX] = {
    signCreator.createSign(tx)
  }

  override def createSign(tx: WavesTransaction): Sign[WavesTransaction] = {
    tx match {
      case tx: GenesisTransaction => implicitlyCreateSign(tx)
      case tx: PaymentTransaction => implicitlyCreateSign(tx)
      case tx: IssueTransaction => implicitlyCreateSign(tx)
      case tx: ReissueTransaction => implicitlyCreateSign(tx)
      case tx: TransferTransaction => implicitlyCreateSign(tx)
    }
  }
}
