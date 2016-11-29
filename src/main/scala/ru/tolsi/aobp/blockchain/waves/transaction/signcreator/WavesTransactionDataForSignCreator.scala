package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.{Sign, DataForSignCreator, WavesBlockChain}
import ru.tolsi.aobp.blockchain.waves.transaction._


private[waves] class WavesTransactionDataForSignCreator extends DataForSignCreator[WavesTransaction] {
  private def implicitlyCreateSign[TX <: WavesTransaction](tx: TX)(implicit signCreator: DataForSignCreator[TX]): Sign[TX] = {
    signCreator.createDataForSign(tx)
  }

  override def createDataForSign(tx: WavesTransaction): Sign[WavesTransaction] = {
    tx match {
      case tx: GenesisTransaction => implicitlyCreateSign(tx)
      case tx: PaymentTransaction => implicitlyCreateSign(tx)
      case tx: IssueTransaction => implicitlyCreateSign(tx)
      case tx: ReissueTransaction => implicitlyCreateSign(tx)
      case tx: TransferTransaction => implicitlyCreateSign(tx)
    }
  }
}
