package com.valoy.overplay.presentation.main

import app.cash.turbine.turbineScope
import com.valoy.overplay.CoroutineMainDispatcherRule
import com.valoy.overplay.domain.models.Gyroscope
import com.valoy.overplay.domain.repository.RotationRepository
import com.valoy.overplay.infra.repository.SessionDataStoreRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    private lateinit var viewModel: MainViewModel
    private val rotationRepository: RotationRepository = mockk(relaxed = true)
    private val sessionRepository: SessionDataStoreRepository = mockk(relaxed = true)

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule(StandardTestDispatcher())
    private val testCoroutineScope = TestScope(coroutineRule.dispatcher)


    @Before
    fun setUp() {
        viewModel = MainViewModel(
            rotationRepository,
            sessionRepository,
            SESSION_TIMEOUT,
            coroutineRule.dispatcher
        )
    }

    @Test
    fun `on new session`() = testCoroutineScope.runTest {
        coEvery { sessionRepository.getTime() } returns System.currentTimeMillis()
        coEvery { sessionRepository.getCount() } returns ZERO

        viewModel.onResume()

        turbineScope {
            val state = viewModel.uiState.testIn(backgroundScope)
            val initial = state.awaitItem()
            val newSession = state.awaitItem()
            assertEquals(INITIAL_SESSION_STATE, initial)
            assertEquals(NEW_SESSION_STATE, newSession)
        }
    }

    @Test
    fun `on rotation change`() = testCoroutineScope.runTest {
        coEvery { rotationRepository.get() } returns flowOf(
            DEFAULT_GYROSCOPE,
            LEFT_GYROSCOPE,
            RIGHT_GYROSCOPE
        )

        viewModel.onResume()

        turbineScope {
            val state = viewModel.uiState.testIn(backgroundScope)
            val initial = state.awaitItem()
            val default = state.awaitItem()
            val twelve = state.awaitItem()
            val twenty = state.awaitItem()
            assertEquals(INITIAL_SIZE_STATE, initial)
            assertEquals(DEFAULT_SIZE_STATE, default)
            assertEquals(TWELVE_SIZE_STATE, twelve)
            assertEquals(TWENTY_SIZE_STATE, twenty)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `on stop invoked`() = testCoroutineScope.runTest {
        viewModel.onStop()

        advanceUntilIdle()

        coVerify(exactly = 1) { sessionRepository.saveTime(any()) }
        coVerify(exactly = 1) { rotationRepository.flush() }
    }


    private companion object {
        const val ZERO = 0
        const val ONE = 1
        const val DEFAULT_SIZE = 16
        const val TWELVE_SIZE = 12
        const val TWENTY_SIZE = 20
        const val SESSION_TIMEOUT = 1
        val DEFAULT_GYROSCOPE = Gyroscope(0.0, 0.0, -50.0)
        val LEFT_GYROSCOPE = Gyroscope(0.0, 0.0, 35.0)
        val RIGHT_GYROSCOPE = Gyroscope(0.0, 0.0, -25.0)
        val INITIAL_SESSION_STATE =
            MainUiState(letterSize = DEFAULT_SIZE, sessionCount = ZERO)
        val NEW_SESSION_STATE =
            MainUiState(letterSize = DEFAULT_SIZE, sessionCount = ONE)
        val INITIAL_SIZE_STATE =
            MainUiState(letterSize = DEFAULT_SIZE, sessionCount = ZERO)
        val DEFAULT_SIZE_STATE =
            MainUiState(letterSize = DEFAULT_SIZE, sessionCount = ONE)
        val TWELVE_SIZE_STATE =
            MainUiState(letterSize = TWELVE_SIZE, sessionCount = ONE)
        val TWENTY_SIZE_STATE =
            MainUiState(letterSize = TWENTY_SIZE, sessionCount = ONE)
    }

}