package com.lingyunchi.fartlek.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.lingyunchi.fartlek.utils.DarkTheme
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsVM : ViewModel() {
    private val _darkMode = MutableStateFlow(DarkTheme.System)
    val darkMode: StateFlow<DarkTheme> get() = _darkMode

    init {
        _darkMode.value = Paper.book("settings").read("isDarkMode", DarkTheme.System)!!
        Log.i("SettingsVM", "init isDarkMode: ${_darkMode.value}")
    }

    fun setDarkMode(darkMode: DarkTheme) {
        _darkMode.value = darkMode
        Paper.book("settings").write("isDarkMode", darkMode)
        Log.i("SettingsVM", "setDarkMode: ${_darkMode.value}")
    }
}