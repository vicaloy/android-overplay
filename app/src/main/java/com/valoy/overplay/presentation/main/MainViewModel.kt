package com.valoy.overplay.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valoy.overplay.di.IoDispatcher
import com.valoy.overplay.di.SessionTimeout
import com.valoy.overplay.domain.models.Gyroscope
import com.valoy.overplay.domain.repository.RotationRepository
import com.valoy.overplay.domain.repository.SessionRepository
import com.valoy.overplay.domain.usecase.SessionUseCase
import com.valoy.overplay.infra.sensor.GyroscopeSensorFlow
import com.valoy.overplay.util.tryCatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    gyroscopeSensorFlow: GyroscopeSensorFlow,
    private val rotationRepository: RotationRepository,
    private val sessionUseCase: SessionUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(MainUiState(letterSize = DEFAULT_SIZE, sessionCount = ZERO))
    val uiState = _uiState.asStateFlow()

    /* collectAsStateWithLifecycle() ensures that the Flow is only being collected when necessary.
    Once the Composable leaves the screen or is no longer in the foreground,
    the collection of the Flow is automatically canceled.
     */
    val gyroscopeStateFlow = gyroscopeSensorFlow()
        .map { result ->
            Log.d(
                "MainViewModel", "CoroutineName $CoroutineName " +
                        "GyroscopeSensorFlow result: $result"
            )
        }.onCompletion { Log.d("MainViewModel", "Fetching gyroscopeSensor complete") }
        .onStart {
            Log.d("MainViewModel", "Fetching gyroscopeSensor started")
        }
        .catch { exception ->
            Log.d("MainViewModel", "Exception: $exception")
        }
        .stateIn(
            scope = viewModelScope,           // The coroutine scope for the ViewModel
            started = SharingStarted.WhileSubscribed(5000), // Start while there are active collectors
            initialValue = Gyroscope(0.0, 0.0, 0.0)  // Initial value before data is emitted
        )

    fun onResume() {
        startNewSession()

        // Start listening to the gyroscope sensor by taking care about the activity lifecycle
        startListeningGyroscope()
    }

    fun onStop() {
        saveSessionTime()

        // Stop listening to the gyroscope sensor by taking care about the activity lifecycle
        onStopListeningGyroscope()
    }

    private fun startListeningGyroscope() {
        viewModelScope.launch(dispatcher) {
            tryCatch {
                rotationRepository.get().collect { degrees ->
                    _uiState.update { state ->
                        state.copy(letterSize = calculateLetterSize((degrees as Gyroscope).z))
                    }
                }
            }
        }
    }

    private fun onStopListeningGyroscope() {
        rotationRepository.flush()
    }

    private fun saveSessionTime() {
        viewModelScope.launch(dispatcher) {
            tryCatch {
                sessionUseCase.saveSessionTime()
            }
        }
    }

    private fun startNewSession() {
        viewModelScope.launch(dispatcher) {
            tryCatch {
                sessionUseCase.startNewSessionCount()
                val count = sessionUseCase.getSessionCount()
                _uiState.update { state ->
                    state.copy(sessionCount = count)
                }
            }
        }
    }

    private fun calculateLetterSize(degrees: Double): Int = when {
        degrees >= THIRTY_DEGREES_LEFT -> TWELVE_SIZE
        degrees >= THIRTY_DEGREES_RIGHT -> TWENTY_SIZE
        else -> DEFAULT_SIZE
    }

    private companion object {
        const val ZERO = 0
        const val DEFAULT_SIZE = 16
        const val TWELVE_SIZE = 12
        const val TWENTY_SIZE = 20
        const val THIRTY_DEGREES_LEFT = 30
        const val THIRTY_DEGREES_RIGHT = -30
    }
}
