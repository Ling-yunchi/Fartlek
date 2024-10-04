package com.lingyunchi.fartlek.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lingyunchi.fartlek.MainSub
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainVM : ViewModel() {
    var pageKey = MutableStateFlow(MainSub.Run)

    fun navigateTo(pageKey: MainSub) {
        this.pageKey.update { pageKey }
    }
}