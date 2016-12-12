package ru.tolsi.aobp.blockchain.waves.model.transaction

import ru.tolsi.aobp.blockchain.waves.model.Currency._
import ru.tolsi.aobp.blockchain.waves.model._

trait Transaction {
  def timestamp: Timestamp
}

trait FromToTransaction extends Transaction {
  def sender: Address
  def recipient: Address
  def fee: Volume
  def quantity: Volume
}
