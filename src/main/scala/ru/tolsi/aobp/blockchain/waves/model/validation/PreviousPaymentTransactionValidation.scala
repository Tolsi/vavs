package ru.tolsi.aobp.blockchain.waves.model.validation

import cats.data.Validated._
import ru.tolsi.aobp.blockchain.waves.model._
import ru.tolsi.aobp.blockchain.waves.model.state.Storage
import ru.tolsi.aobp.blockchain.waves.model.transaction.PaymentTransaction

object PreviousPaymentTransactionValidation {
  def apply(startTime: Timestamp)(t: PaymentTransaction): FreeValidationResult[PaymentTransaction] =
    for {
      time <- Storage.lastConfirmedBlockTimestamp()
      r <- if (time >= startTime)
        Storage.pure(valid(t))
      else {
        for {
          maybeLastTx <- Storage.previousPaymentTransactionTimestamp(t.sender)
        } yield
          maybeLastTx match {
            case Some(lastTx) if lastTx >= t.timestamp =>
              invalidNel("Transaction timestamp is in the past")
            case _ => valid(t)
          }

      }
    } yield r
}
