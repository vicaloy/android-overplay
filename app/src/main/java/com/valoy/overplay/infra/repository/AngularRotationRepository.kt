package com.valoy.overplay.infra.repository

import com.valoy.overplay.domain.repository.RotationRepository
import com.valoy.overplay.infra.sensor.SensorListener
import javax.inject.Inject

class AngularRotationRepository @Inject constructor(
    private val gyroscopeListener: SensorListener,
) : RotationRepository {

    override suspend fun get() = gyroscopeListener.subscribe()

    override fun flush() {
        gyroscopeListener.unsubscribe()
    }
}
