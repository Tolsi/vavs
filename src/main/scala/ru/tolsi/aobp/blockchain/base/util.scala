package ru.tolsi.aobp.blockchain.base

/**
  * Hash algorithm with low collision rate and high performance (https://habrahabr.ru/post/219139/)
  */
private[blockchain] object LyHash {
  def compute(bytes: Array[Byte]): Int = {
    bytes.foldLeft(0)((hash, byte) =>
      (hash * 1664525) + byte + 1013904223
    )
  }
}
