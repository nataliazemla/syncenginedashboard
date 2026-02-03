package com.example.syncenginedashboard.data.remote

import com.example.syncenginedashboard.domain.SyncSource
import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.random.Random

class FakeRemoteDataSource(
    private val latencyMs: Long = 400,
    private val failureRate: Double = 0.2, // 0.0..1.0
    private val random: Random = Random.Default
) {
    init {
        require(latencyMs >= 0) { "latencyMs must be >= 0" }
        require(failureRate in 0.0..1.0) { "failureRate must be in 0.0..1.0" }
    }

    suspend fun fetch(source: SyncSource): List<String> {
        delay(latencyMs)

        if (random.nextDouble() < failureRate) {
            throw IOException("Simulated network failure for $source")
        }

        return List(5) { idx -> "${source.name}-item-${idx + 1}" }
    }
}