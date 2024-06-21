package com.valoy.overplay.infra.sensor

import com.valoy.overplay.domain.models.Sensor
import kotlinx.coroutines.flow.Flow

interface SensorListener {
    suspend fun subscribe(): Flow<Sensor>
    fun unsubscribe()
}
