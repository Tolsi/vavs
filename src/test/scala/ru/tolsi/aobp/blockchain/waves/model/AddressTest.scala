package ru.tolsi.aobp.blockchain.waves.model

import org.scalatest.{FlatSpec, Matchers}
import ru.tolsi.aobp.blockchain.waves.testdata.{ValidationError, ValidationSuccess}
import scorex.crypto.encode.Base58

class AddressTest extends FlatSpec with Matchers {

  "Address" should "be reconstructed from valid Base58 string" in {
    Address(Base58.decode("3P31zvGdh6ai6JK6zZ18TjYzJsa1B83YPoj").get) shouldBe a[ValidationSuccess]
  }
  it should "not be reconstructed if hash is invalid" in {
    Address(Base58.decode("3P31zvGdh6ai6JK6zZ18TjYzJsa1B83YPo3").get) shouldBe a[ValidationError]
  }
}
