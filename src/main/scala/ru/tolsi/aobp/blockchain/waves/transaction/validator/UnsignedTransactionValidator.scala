package ru.tolsi.aobp.blockchain.waves.transaction.validator

import ru.tolsi.aobp.blockchain.waves.transaction._
import ru.tolsi.aobp.blockchain.waves.transaction.validator.error.WrongState
import ru.tolsi.aobp.blockchain.waves.{TransactionValidationError, WavesBlockChain}

private[validator] class UnsignedTransactionValidator extends TransactionValidator[WavesTransaction] {
  private def implicitlyValidate[TX <: WavesTransaction](tx: TX)(implicit wbc: WavesBlockChain, validator: TransactionValidator[TX]): Either[Seq[TransactionValidationError[WavesTransaction]], WavesTransaction] = {
    validator.validate(tx)
  }

  override def validate(tx: WavesTransaction)(implicit wbc: WavesBlockChain):
  Either[Seq[TransactionValidationError[WavesTransaction]], WavesTransaction] = {
    val txValidation = tx match {
      // todo all state changes validation
      case tx: GenesisTransaction => implicitlyValidate(tx)
      case tx: PaymentTransaction => implicitlyValidate(tx)
      case tx: IssueTransaction => implicitlyValidate(tx)
      case tx: ReissueTransaction => implicitlyValidate(tx)
      case tx: TransferTransaction => implicitlyValidate(tx)
    }
    txValidation.right.flatMap(t => {
      if (wbc.stateStorage.isLeadToValidState(t)) {
        Right(t)
      } else {
        Left(Seq(new WrongState("Transaction lead to invalid state")))
      }
    })
  }
}
