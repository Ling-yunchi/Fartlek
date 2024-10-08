package com.lingyunchi.fartlek.context

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("NavController not provided")
}

@Composable
fun LocalSnackbarHostStateProvider(
    snackbarHostState: SnackbarHostState,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        content()
    }
}