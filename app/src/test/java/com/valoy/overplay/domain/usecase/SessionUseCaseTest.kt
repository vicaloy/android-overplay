package com.valoy.overplay.domain.usecase

import com.valoy.overplay.CoroutineMainDispatcherRule
import com.valoy.overplay.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SessionUseCaseTest {

    private lateinit var sessionUseCase: SessionUseCase
    private val sessionRepository: SessionRepository = mockk(relaxed = true)

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule(StandardTestDispatcher())
    private val testCoroutineScope = TestScope(coroutineRule.dispatcher)


    @Before
    fun setUp() {
        sessionUseCase = SessionUseCase(
            sessionRepository,
            SESSION_TIMEOUT,
        )
    }

    @Test
    fun `on session start new session invoked`() = testCoroutineScope.runTest {
        coEvery { sessionRepository.getTime() } returns System.currentTimeMillis()
        coEvery { sessionRepository.getCount() } returns ZERO

        sessionUseCase.startNewSessionCount()

        coVerify(exactly = 1) { sessionRepository.getTime() }
        coVerify(exactly = 1) { sessionRepository.getCount() }
        coVerify(exactly = 1) { sessionRepository.saveCount(ONE) }
    }

    @Test
    fun `on get session count invoked`() = testCoroutineScope.runTest {
        sessionUseCase.getSessionCount()

        coVerify(exactly = 1) { sessionRepository.getCount() }
    }

    @Test
    fun `on save session time invoked`() = testCoroutineScope.runTest {
        sessionUseCase.saveSessionTime()

        coVerify(exactly = 1) { sessionRepository.saveTime(any()) }
    }

    private companion object {
        const val ZERO = 0
        const val ONE = 1
        const val SESSION_TIMEOUT = 1
    }
}
