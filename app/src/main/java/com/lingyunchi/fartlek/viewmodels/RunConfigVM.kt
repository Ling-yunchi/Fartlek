package com.lingyunchi.fartlek.viewmodels

import androidx.lifecycle.ViewModel
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class RunConfig(
    val id: Int, val name: String, val duration: Int, val intervals: List<Interval>
) {
    data class Interval(val runMinutes: Int, val walkMinutes: Int)
}

class RunConfigVM : ViewModel() {
    private val _runConfigs = MutableStateFlow(listOf<RunConfig>())
    val runConfigs: StateFlow<List<RunConfig>> get() = _runConfigs

    private val _selectedConfigId = MutableStateFlow(-1)
    val selectedConfigId: StateFlow<Int> get() = _selectedConfigId

    init {
        loadRunConfigs()
    }

    private fun generateUniqueId(): Int {
        return (_runConfigs.value.maxOfOrNull { it.id } ?: 0) + 1
    }

    fun updateRunConfig(runConfig: RunConfig): Boolean {
        if (runConfig.id < 0) {
            val newId = generateUniqueId()
            _runConfigs.value += listOf(runConfig.copy(id = newId))
        } else {
            val existingRunConfig = _runConfigs.value.find { it.id == runConfig.id }
            if (existingRunConfig == null) return false
            _runConfigs.value = _runConfigs.value.map {
                if (it.id == runConfig.id) runConfig else it
            }
        }
        saveRunConfigs()
        return true
    }

    fun removeRunConfig(id: Int) {
        _runConfigs.value = _runConfigs.value.filter { it.id != id }
        saveRunConfigs()
    }

    fun selectConfig(id: Int) {
        val existingRunConfig = _runConfigs.value.find { it.id == id }
        if (existingRunConfig == null) throw IllegalArgumentException("id: $id not found")
        _selectedConfigId.value = id
        saveRunConfigs()
    }

    private fun saveRunConfigs() {
        Paper.book("run-config").write("run-configs", _runConfigs.value)
        Paper.book("run-config").write("selected-config-id", _selectedConfigId.value)
    }

    private fun loadRunConfigs() {
        _runConfigs.value = Paper.book("run-config").read(
            "run-configs", listOf(
                RunConfig(
                    id = 0, name = "Default 30 Min", duration = 30, intervals = listOf(
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 2, walkMinutes = 1),
                    )
                ), RunConfig(
                    id = 1, name = "Default 60 Min", duration = 60, intervals = listOf(
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                        RunConfig.Interval(runMinutes = 5, walkMinutes = 1),
                    )
                )
            )
        )!!
        _selectedConfigId.value = Paper.book("run-config").read("selected-config-id", -1)!!
    }
}