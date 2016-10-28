package ru.tolsi.aobp.blockchain

import ru.tolsi.aobp.blockchain.base.{Signature32, Signature64}

package object waves {
  type Seed = Array[Byte]
  type PrivateKey = Array[Byte]
  type PublicKey = Array[Byte]
  type ArraySignature32 = Signature32[Array[Byte]]
  type ArraySignature64 = Signature64[Array[Byte]]
}
