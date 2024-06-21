package com.valoy.overplay.di

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import com.valoy.overplay.domain.repository.RotationRepository
import com.valoy.overplay.domain.repository.SessionRepository
import com.valoy.overplay.infra.repository.AngularRotationRepository
import com.valoy.overplay.infra.sensor.SensorManagerUnavailableException
import com.valoy.overplay.infra.repository.SessionDataStoreRepository
import com.valoy.overplay.infra.sensor.GyroscopeSensorListener
import com.valoy.overplay.infra.sensor.SensorListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideSensorManager(context: Context): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            ?: throw SensorManagerUnavailableException()
    }

    @Provides
    fun provideGyroscopeListener(sensorManager: SensorManager): SensorListener {
        return GyroscopeSensorListener(sensorManager)
    }

    @Provides
    fun provideGyroscopeSensorRepository(gyroscopeListener: SensorListener): RotationRepository {
        return AngularRotationRepository(gyroscopeListener)
    }

    @Provides
    fun provideSessionDataStoreRepository(context: Context): SessionRepository {
        return SessionDataStoreRepository(context)
    }

    @Provides
    @SessionTimeout
    fun provideSessionTimeout(): Int = 10 * 60 * 1000
}
