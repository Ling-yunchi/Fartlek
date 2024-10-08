@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.lingyunchi.fartlek.views

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingyunchi.fartlek.MainSub

@Composable
fun MainView(
    navigateTo: (Any) -> Unit,
) {
    val mainVM = viewModel<MainVM>()
    val pageKey by mainVM.pageKey.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        bottomBar = { BottomBar(pageKey) { mainVM.navigateTo(it) } },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (pageKey) {
                MainSub.Run -> Run()
                MainSub.Logs -> Logs()
                MainSub.Settings -> Settings()
            }
        }
    }
}

@Composable
fun TopBar() {
    val settingsVM: SettingsVM = viewModel(LocalContext.current as ViewModelStoreOwner)
    val darkMode by settingsVM.darkMode.collectAsState()
    Log.d("TopBar", "TopBar: ${settingsVM.hashCode()}")
    Log.d("TopBar", "${LocalContext.current}")

    LaunchedEffect(darkMode) {
        Log.d("TopBar", "darkMode changed: $darkMode")
    }

    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(text = darkMode.toString()) },
    )
}

@Composable
fun BottomBar(pageKey: MainSub, navigateTo: (MainSub) -> Unit) {
    val tabs = listOf(
        Triple(MainSub.Run, Icons.Filled.Home, Icons.Outlined.Home),
        Triple(MainSub.Logs, Icons.Filled.DateRange, Icons.Outlined.DateRange),
        Triple(MainSub.Settings, Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    NavigationBar {
        for (tab in tabs) {
            NavigationBarItem(
                onClick = {
                    navigateTo(tab.first)
                },
                selected = tab.first == pageKey,
                icon = {
                    Icon(
                        if (tab.first == pageKey) tab.second else tab.third,
                        contentDescription = tab.first.toString(),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                label = { Text(text = tab.first.toString()) },
            )
        }
    }
}