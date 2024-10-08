package com.lingyunchi.fartlek

import kotlinx.serialization.Serializable

@Serializable
object Main

enum class MainSub {
    Run,
    Configs,
    Logs,
    Settings;

    override fun toString(): String {
        return when (this) {
            Run -> "Run"
            Configs -> "Configs"
            Logs -> "Logs"
            Settings -> "Settings"
        }
    }
}

@Serializable
data class ConfigEdie(val id: Int = -1)

@Serializable
object Running

@Serializable
data class RunFinished(val startTime: Long, val duration: Long, val configId: Int)