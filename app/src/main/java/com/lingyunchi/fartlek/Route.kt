package com.lingyunchi.fartlek

import kotlinx.serialization.Serializable

@Serializable
object Main

enum class MainSub {
    Run,
    Logs,
    Settings;

    override fun toString(): String {
        return when (this) {
            Run -> "run"
            Logs -> "logs"
            Settings -> "settings"
        }
    }
}
