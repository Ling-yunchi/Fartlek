package com.lingyunchi.fartlek.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

    private val _currentIntervalIndex = _currentPhaseIndex.map { it / 2 }
    val currentIntervalIndex =
        _currentIntervalIndex.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime = _elapsedTime.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _startTime = MutableStateFlow(0L)
    val startTime = _startTime.asStateFlow()

    private val _progress = _elapsedTime.map { it / _totalDuration.value.toDouble() }
    val progress = _progress.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

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
    }

    fun pause() {
        _isRunning.value = false
    }

    fun tick(delta: Long) {
        if (!_isRunning.value) return
        _elapsedTime.value += delta
        _currentPhaseDurationRemaining.value -= delta

        if (_currentPhaseDurationRemaining.value <= 0) {
            _currentPhaseIndex.value += 1
            if (_currentPhaseIndex.value < _currentRunConfig.value!!.intervals.size * 2) {
                val nextInterval = _currentRunConfig.value!!.intervals[_currentPhaseIndex.value / 2]
                _currentPhaseDurationRemaining.value =
                    if (_currentPhaseIndex.value % 2 == 0) {
                        nextInterval.runMinutes.toLong() * 60 * 1000
                    } else {
                        nextInterval.walkMinutes.toLong() * 60 * 1000
                    }
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
    }
}