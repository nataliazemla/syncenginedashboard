package com.example.syncenginedashboard.data.local

import com.example.syncenginedashboard.domain.SyncSource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryLocalDataSource {
    private val mutex = Mutex()
    private val storage: MutableMap<SyncSource, List<String>> = mutableMapOf()

    suspend fun save(source: SyncSource, items: List<String>) {
        mutex.withLock {
            storage[source] = items
        }
    }

    suspend fun read(source: SyncSource): List<String> {
        return mutex.withLock { storage[source].orEmpty() }
    }
}