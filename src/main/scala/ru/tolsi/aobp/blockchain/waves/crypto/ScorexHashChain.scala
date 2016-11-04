package ru.tolsi.aobp.blockchain.waves.crypto

import scorex.crypto._
import scorex.crypto.hash.{Blake256, CryptographicHash, Keccak256}

private[waves] object ScorexHashChain extends CryptographicHash {
  override val DigestSize: Int = 32

  override def hash(in: Message): Digest = applyHashes(in, Blake256, Keccak256)
}

