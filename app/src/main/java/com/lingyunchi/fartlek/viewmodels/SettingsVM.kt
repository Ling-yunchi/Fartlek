package com.lingyunchi.fartlek.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.lingyunchi.fartlek.utils.DarkTheme
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsVM : ViewModel() {
    private val _darkMode = MutableStateFlow(DarkTheme.System)
    val darkMode: StateFlow<DarkTheme> get() = _darkMode

    private val _runVoiceAnnouncement = MutableStateFlow(listOf<Long>())
    val runVoiceAnnouncement = _runVoiceAnnouncement.asStateFlow()

    private val _walkVoiceAnnouncement = MutableStateFlow(listOf<Long>())
    val walkVoiceAnnouncement = _walkVoiceAnnouncement.asStateFlow()

    init {
        _darkMode.value = Paper.book("settings").read("isDarkMode", DarkTheme.System)!!
        _runVoiceAnnouncement.value =
            Paper.book("settings").read("runVoiceAnnouncement", listOf())!!
        _walkVoiceAnnouncement.value =
            Paper.book("settings").read("walkVoiceAnnouncement", listOf())!!
    }

    fun setDarkMode(darkMode: DarkTheme) {
        _darkMode.value = darkMode
        Paper.book("settings").write("isDarkMode", darkMode)
        Log.i("SettingsVM", "setDarkMode: ${_darkMode.value}")
    }

    fun setRunVoiceAnnouncement(list: List<Long>) {
        _runVoiceAnnouncement.value = list
        Paper.book("settings").write("runVoiceAnnouncement", list)
    }

    fun setWalkVoiceAnnouncement(list: List<Long>) {
        _walkVoiceAnnouncement.value = list
        Paper.book("settings").write("walkVoiceAnnouncement", list)
    }
}