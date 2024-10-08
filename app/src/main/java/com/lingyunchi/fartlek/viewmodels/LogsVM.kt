package com.lingyunchi.fartlek.viewmodels

import androidx.lifecycle.ViewModel
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
data class RunLog(val id: Int, val startTime: Long, val duration: Long, val configId: Int)

class LogsVM : ViewModel() {
    private val _logs = MutableStateFlow(listOf<RunLog>())
    val logs: StateFlow<List<RunLog>> get() = _logs

    init {
        loadLogs()
    }

    fun generateId(): Int {
        return (_logs.value.maxOfOrNull { it.id } ?: 0) + 1
    }

    fun addLog(log: RunLog) {
        _logs.value += listOf(log)
        saveLogs()
    }

    fun removeLog(id:Int) {
        _logs.value = _logs.value.filter { it.id != id }
        saveLogs()
    }

    fun saveLogs() {
        Paper.book("run-logs").write("logs", _logs.value)
    }

    fun loadLogs() {
        _logs.value = Paper.book("run-logs").read("logs", listOf())!!
    }
}