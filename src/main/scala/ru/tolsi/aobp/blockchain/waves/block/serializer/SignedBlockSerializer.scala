package ru.tolsi.aobp.blockchain.waves.block.serializer

import com.google.common.primitives.Bytes
import ru.tolsi.aobp.blockchain.base.bytes.BytesSerializer
import ru.tolsi.aobp.blockchain.waves.SignedBlock
import ru.tolsi.aobp.blockchain.waves.block.WavesBlock
import ru.tolsi.aobp.blockchain.waves.block.signer.WavesBlockDataForSignCreator

class SignedBlockSerializer(dataForSignCreator: WavesBlockDataForSignCreator) extends BytesSerializer[SignedBlock[WavesBlock]] {
  override def serialize(sb: SignedBlock[WavesBlock]): Array[Byte] = {
    Bytes.concat(dataForSignCreator.serialize(sb.signed), sb.signature.value)
  }
}
