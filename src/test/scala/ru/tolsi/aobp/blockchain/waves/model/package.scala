package ru.tolsi.aobp.blockchain.waves

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import ru.tolsi.aobp.blockchain.waves.model.Timestamp
import ru.tolsi.aobp.blockchain.waves.model.transaction.Transaction

/**
  * Created by ilya on 09.12.16.
  */
package object testdata {
  case class BananaTranscation(timestamp: Timestamp) extends Transaction

  type ValidationError = Invalid[NonEmptyList[String]]
  type ValidationSuccess = Valid[_]
}
