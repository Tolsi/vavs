package ru.tolsi.aobp.blockchain.waves.model.validation

import cats.data.Validated._
import com.github.nscala_time.time.Imports._
import ru.tolsi.aobp.blockchain.waves.model.state.Storage
import ru.tolsi.aobp.blockchain.waves.model.transaction.Transaction
import ru.tolsi.aobp.blockchain.waves.model.{FreeValidationResult, _}

object MaxTimeUnconfirmedValidation {

  private val MaxTimeForUnconfirmed = 90.minutes.millis

  def apply[T <: Transaction](ruleStartTime: Timestamp)(t: T): FreeValidationResult[T] =
    for {
      time <- Storage.lastConfirmedBlockTimestamp()
      r <- if (time > ruleStartTime && t.timestamp - time > MaxTimeForUnconfirmed)
        invalidNel("Transaction creation time more then block's creation time no more then on MaxTimeForUnconfirmed")
      else valid(t)
    } yield r
}
