package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.base.{TransactionValidationError, TransactionValidator}
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.transaction._

private[validator] class UnsignedTransactionValidator extends TransactionValidator[WavesBlockChain, WavesBlockChain#T] {
  private def implicitlyValidate[TX <: WavesBlockChain#T](tx: TX)(implicit validator: AbstractTransactionValidator[TX]): Either[Seq[TransactionValidationError[WavesBlockChain, TX]], WavesBlockChain#T] = {
    validator.validate(tx)
  }

  override def validate(tx: WavesBlockChain#T)(implicit wbc: WavesBlockChain):
  Either[Seq[TransactionValidationError[WavesBlockChain, WavesBlockChain#T]], WavesTransaction] = {
    tx match {
      // todo all state changes validation
      case tx: GenesisTransaction => implicitlyValidate(tx)
      case tx: PaymentTransaction => implicitlyValidate(tx)
      case tx: IssueTransaction => implicitlyValidate(tx)
      case tx: ReissueTransaction => implicitlyValidate(tx)
      case tx: TransferTransaction => implicitlyValidate(tx)
    }
  }
}
