package ru.tolsi.aobp.blockchain.waves.model.state

import cats.data._
import cats.free._
import ru.tolsi.aobp.blockchain.waves.model.Currency.Portfolio
import ru.tolsi.aobp.blockchain.waves.model._

import scala.language.implicitConversions

object Storage {

  trait DSL[A]

  case class LastConfirmedBlockTimestamp()     extends DSL[Timestamp]
  case class PrevPaymentTransactionTimestamp() extends DSL[Option[Timestamp]]

  def lastConfirmedBlockTimestamp(): Free[DSL, Timestamp]                           = Free.liftF(LastConfirmedBlockTimestamp())
  def previousPaymentTransactionTimestamp(a: Address): Free[DSL, Option[Timestamp]] = Free.liftF(PrevPaymentTransactionTimestamp())
  def accBalance(a: Address): Free[DSL, Portfolio]                                  = ???

  implicit def pure[A](a: A): Free[DSL,A] = Free.pure(a)

}
