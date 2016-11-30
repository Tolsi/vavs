package ru.tolsi.aobp.blockchain.waves.transaction.signcreator

import ru.tolsi.aobp.blockchain.waves.DataForSignCreator
import ru.tolsi.aobp.blockchain.waves.transaction._


private[waves] class WavesTransactionDataForSignCreator extends DataForSignCreator[WavesTransaction] {
  private def implicitlyCreateSign[TX <: WavesTransaction](tx: TX)(implicit dataForSignCreator: DataForSignCreator[TX]): Array[Byte] = {
    dataForSignCreator.serialize(tx)
  }

  override def serialize(tx: WavesTransaction): Array[Byte] = {
    tx match {
      case tx: GenesisTransaction => implicitlyCreateSign(tx)
      case tx: PaymentTransaction => implicitlyCreateSign(tx)
      case tx: IssueTransaction => implicitlyCreateSign(tx)
      case tx: ReissueTransaction => implicitlyCreateSign(tx)
      case tx: TransferTransaction => implicitlyCreateSign(tx)
    }
  }
}
