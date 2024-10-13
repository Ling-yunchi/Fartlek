package com.lingyunchi.fartlek.viewmodels

import androidx.lifecycle.ViewModel
import com.lingyunchi.fartlek.utils.EventSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RunningVM : ViewModel() {
    // run config
    private val _currentRunConfig = MutableStateFlow<RunConfig?>(null)
    val currentRunConfig = _currentRunConfig.asStateFlow()

    // running state
    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration = _totalDuration.asStateFlow()

    private val _currentPhaseIndex = MutableStateFlow(0)
    val currentPhaseIndex = _currentPhaseIndex.asStateFlow()

    private val _currentPhaseDurationRemaining = MutableStateFlow(0L)
    val currentPhaseDurationRemaining = _currentPhaseDurationRemaining.asStateFlow()

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime = _elapsedTime.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _startTime = MutableStateFlow(0L)
    val startTime = _startTime.asStateFlow()

    val onNotification = EventSource<String>()
    val onStop = EventSource<Unit>()

    data class CurrentPhaseDurationRemainingArgs(
        val currentPhaseIndex: Int, val lastTime: Long, val currentTime: Long
    )

    val onCurrentPhaseDurationRemainingChange = EventSource<CurrentPhaseDurationRemainingArgs>()

    val onPhaseChange = EventSource<Int>()


    fun setRunConfig(runConfig: RunConfig) {
        _currentRunConfig.value = runConfig
        _totalDuration.value =
            runConfig.intervals.sumOf { it.runMinutes + it.walkMinutes }.toLong() * 60 * 1000
        _currentPhaseIndex.value = 0
        _currentPhaseDurationRemaining.value =
            runConfig.intervals.first().runMinutes.toLong() * 60 * 1000
        _elapsedTime.value = 0
    }

    fun start() {
        if (_currentRunConfig.value == null) throw IllegalStateException("No run config")
        _isRunning.value = true
        _startTime.value = System.currentTimeMillis()
        onPhaseChange.emit(_currentPhaseIndex.value)
    }

    fun pause(run: Boolean) {
        _isRunning.value = run
    }

    fun tick(delta: Long) {
        if (!_isRunning.value) return
        _elapsedTime.value += delta
        val lastPhaseDurationRemaining = _currentPhaseDurationRemaining.value
        _currentPhaseDurationRemaining.value -= delta

        onNotification.emit(
            "${_currentPhaseIndex.value / 2 + 1} / ${_currentRunConfig.value!!.intervals.size} ${
                if (_currentPhaseIndex.value % 2 == 0) "run" else "walk"
            } ${_currentPhaseDurationRemaining.value / 1000}s"
        )

        onCurrentPhaseDurationRemainingChange.emit(
            CurrentPhaseDurationRemainingArgs(
                _currentPhaseIndex.value,
                lastPhaseDurationRemaining,
                _currentPhaseDurationRemaining.value
            )
        )

        if (_currentPhaseDurationRemaining.value <= 0) {
            _currentPhaseIndex.value += 1
            if (_currentPhaseIndex.value < _currentRunConfig.value!!.intervals.size * 2) {
                val nextInterval = _currentRunConfig.value!!.intervals[_currentPhaseIndex.value / 2]
                _currentPhaseDurationRemaining.value = if (_currentPhaseIndex.value % 2 == 0) {
                    nextInterval.runMinutes.toLong() * 60 * 1000
                } else {
                    nextInterval.walkMinutes.toLong() * 60 * 1000
                }
                onPhaseChange.emit(_currentPhaseIndex.value)
            } else {
                stop()
            }
        }
    }

    fun stop() {
        _isRunning.value = false
        _currentPhaseIndex.value = 0
        _currentPhaseDurationRemaining.value = 0
        _elapsedTime.value = 0
        _startTime.value = 0
        onStop.emit(Unit)
    }
}