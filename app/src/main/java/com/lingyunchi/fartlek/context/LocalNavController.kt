package com.lingyunchi.fartlek.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> {
    error("NavController not provided")
}

@Composable
fun LocalNavControllerProvider(navController: NavController, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNavController provides navController) {
        content()
    }
}