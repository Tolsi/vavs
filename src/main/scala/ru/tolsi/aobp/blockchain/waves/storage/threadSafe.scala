package ru.tolsi.aobp.blockchain.waves.storage

sealed trait ThreadSafeStorageValue

trait ThreadSafeStorage extends ThreadSafeStorageValue

trait NotThreadSafeStorage extends ThreadSafeStorageValue
