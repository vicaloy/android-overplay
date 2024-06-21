package com.valoy.overplay.infra.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.valoy.overplay.domain.models.Gyroscope
import com.valoy.overplay.infra.repository.GyroscopeUnavailableException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.math.sqrt

class GyroscopeSensorListener @Inject constructor(private val sensorManager: SensorManager) :
    SensorListener {

    private var listener: SensorEventListener? = null
    private var isRotating = false

    override suspend fun subscribe(): Flow<Gyroscope> = callbackFlow {
        val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (gyroscopeSensor == null) {
            close(GyroscopeUnavailableException("Gyroscope sensor not available"))
            return@callbackFlow
        }

        if (listener == null) {
            listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        if (it.sensor.type == Sensor.TYPE_GYROSCOPE) {
                            val axisX = event.values[0]
                            val axisY = event.values[1]
                            val axisZ = event.values[2]
                            val omegaMagnitude =
                                sqrt((axisX * axisX + axisY * axisY + axisZ * axisZ).toDouble())

                            /*if (omegaMagnitude > ROTATION_THRESHOLD && !isRotating) {
                                isRotating = true
                            } else if (omegaMagnitude <= ROTATION_THRESHOLD && isRotating) {
                                isRotating = false
                            }*/

                            if (omegaMagnitude > ROTATION_THRESHOLD) {
                                trySend(
                                    Gyroscope(
                                        Math.toDegrees(axisX.toDouble()),
                                        Math.toDegrees(axisY.toDouble()),
                                        Math.toDegrees(axisZ.toDouble())
                                    )
                                )
                            }
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
        }

        sensorManager.registerListener(listener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
            listener = null
        }
    }

    override fun unsubscribe() {
        sensorManager.unregisterListener(listener)
        listener = null
    }

    companion object {
        private const val ROTATION_THRESHOLD = 0.9f
    }
}
