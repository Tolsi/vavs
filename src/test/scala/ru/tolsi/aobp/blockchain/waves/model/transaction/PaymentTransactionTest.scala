package ru.tolsi.aobp.blockchain.waves.model.transaction

import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import org.scalatest._
import ru.tolsi.aobp.blockchain.waves.model.Address
import ru.tolsi.aobp.blockchain.waves.model.Currency.WavesVolume
import ru.tolsi.aobp.blockchain.waves.testdata.ValidationSuccess
import scorex.crypto.encode.Base58

class PaymentTransactionTest extends FlatSpec with Matchers {

  def adr1 = Address(Base58.decode("3P31zvGdh6ai6JK6zZ18TjYzJsa1B83YPoj").get).toOption.get
  def adr2 = Address(Base58.decode("3PQYCifXbWmMmqPAhjTxBkPvfDoHjMKmCxZ").get).toOption.get

  "PaymentTransaction" should "not be created with incorrect input" in {
    PaymentTransaction(adr1, adr2, WavesVolume(-100), WavesVolume(-1), 1L) shouldBe
      Invalid(NonEmptyList.of("Quantity must be greater than zero", "Fee must be greater than zero"))
  }

  it should "be created with valid input" in {
    PaymentTransaction(adr1, adr2, WavesVolume(100), WavesVolume(1), 1L) shouldBe a[ValidationSuccess]
  }

}
