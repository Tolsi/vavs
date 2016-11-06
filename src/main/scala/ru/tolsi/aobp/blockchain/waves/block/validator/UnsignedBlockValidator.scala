package ru.tolsi.aobp.blockchain.waves.block.validator

import ru.tolsi.aobp.blockchain.base._
import ru.tolsi.aobp.blockchain.waves.WavesBlockChain
import ru.tolsi.aobp.blockchain.waves.block.validator.error.{WrongState, WrongTransaction}
import ru.tolsi.aobp.blockchain.waves.block.{BaseBlock, GenesisBlock}
import ru.tolsi.aobp.blockchain.waves.transaction.validator.SignedTransactionWithTimeValidator

class UnsignedBlockValidator(tv: Long => SignedTransactionWithTimeValidator) extends AbstractBlockValidator[WavesBlockChain, WavesBlockChain#B] {
  private def implicitlyValidate[BL <: WavesBlockChain#B](b: BL)(implicit wbc: WavesBlockChain, validator: AbstractBlockValidator[WavesBlockChain, BL]): Either[Seq[BlockValidationError[WavesBlockChain, WavesBlockChain#B]], WavesBlockChain#B] = {
    validator.validate(b)
  }

  // todo rewrite validators to returns all errors list (either concat)
  override def validate(b: WavesBlockChain#B)(implicit wbc: WavesBlockChain): Either[Seq[BlockValidationError[WavesBlockChain, WavesBlockChain#B]], WavesBlockChain#B] = {
    val blockValidationResult = b match {
      // todo txs in correct order
      // todo not from future and from last block time, see original waves
      case b: GenesisBlock => implicitlyValidate(b)
      case b: BaseBlock => implicitlyValidate(b)
    }
    val transactionsValidationResult = blockValidationResult.right.flatMap(b => {
      val validator = tv(b.timestamp)
      b.transactions.foldLeft[ResultT](Right[ErrorsSeqT, ValidatedT](b)) {
        case (r, t) =>
          r.right.flatMap(_ => validator.validate(t).fold[ResultT](errors => Left(errors.map(e => new WrongTransaction(e.message))), _ => r))
      }
    })
    val stateValidationResult = transactionsValidationResult.right.flatMap(b =>
      Either.cond(wbc.stateStorage.isLeadToValidState(b), b, Seq(new WrongState("Block transactions leads to illegal state"))))
    stateValidationResult
  }
}

