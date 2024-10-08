package com.lingyunchi.fartlek.viewmodels

import androidx.lifecycle.ViewModel
import com.lingyunchi.fartlek.MainSub
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainVM : ViewModel() {
    private val _pageKey = MutableStateFlow(MainSub.Run)
    val pageKey: StateFlow<MainSub> get() = _pageKey

    fun navigateTo(pageKey: MainSub) {
        this._pageKey.value = pageKey
    }
}