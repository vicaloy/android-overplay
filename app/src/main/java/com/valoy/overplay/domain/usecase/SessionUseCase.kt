package com.valoy.overplay.domain.usecase

import com.valoy.overplay.di.SessionTimeout
import com.valoy.overplay.domain.repository.SessionRepository
import javax.inject.Inject

class SessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    @SessionTimeout private val sessionTimeout: Int,
) {

    suspend fun startNewSessionCount() {
        val time = sessionRepository.getTime()
        val count = sessionRepository.getCount() + ONE
        val elapsedTime = System.currentTimeMillis() - time
        if (shouldStartNewSession(elapsedTime)) {
            sessionRepository.saveCount(count)
        }
    }

    suspend fun getSessionCount() = sessionRepository.getCount()

    suspend fun saveSessionTime() {
        sessionRepository.saveTime(System.currentTimeMillis())
    }

    private fun shouldStartNewSession(elapsedTime: Long): Boolean = elapsedTime > sessionTimeout

    private companion object {
        const val ONE = 1
    }
}
