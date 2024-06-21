package com.valoy.overplay.domain.repository

import com.valoy.overplay.domain.models.Sensor
import kotlinx.coroutines.flow.Flow

interface RotationRepository {
    suspend fun get(): Flow<Sensor>
    fun flush()
}
