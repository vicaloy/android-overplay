package com.valoy.overplay.infra.repository

import com.valoy.overplay.CoroutineMainDispatcherRule
import com.valoy.overplay.domain.repository.RotationRepository
import com.valoy.overplay.infra.sensor.GyroscopeSensorListener
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AngularRotationRepositoryTest {
    private lateinit var repository: RotationRepository
    private val gyroscopeListener: GyroscopeSensorListener = mockk(relaxed = true)

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule(StandardTestDispatcher())
    private val testCoroutineScope = TestScope(coroutineRule.dispatcher)

    @Before
    fun setUp() {
        repository = AngularRotationRepository(gyroscopeListener)
    }

    @Test
    fun `on get invoked`() = testCoroutineScope.runTest {
        repository.get()

        coVerify(exactly = 1) { gyroscopeListener.subscribe() }
    }

    @Test
    fun `on flush invoked`() {
        repository.flush()

        coVerify(exactly = 1) { gyroscopeListener.unsubscribe() }
    }
}