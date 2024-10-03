@file:OptIn(ExperimentalMaterial3Api::class)

package com.lingyunchi.fartlek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lingyunchi.fartlek.ui.theme.FartlekTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FartlekTheme {
                App()
            }
        }
    }
}

enum class Page {
    Logs,
    Run,
    Settings
}

@Composable
fun App() {
    var expanded by remember { mutableStateOf(false) }
    var pageKey by remember { mutableStateOf(Page.Run) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar(pageKey) { pageKey = it }
        },
    ) { paddingValues ->
        Content(pageKey, paddingValues)
    }
}

@Composable
fun Content(key: Page, paddingValues: PaddingValues) {
    when (key) {
        Page.Run -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(64.dp)
            ) {
                Text(text = "Run")
            }
        }

        Page.Logs -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Text(text = "Logs")
            }
        }

        Page.Settings -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Text(text = "Settings")
            }
        }
    }
}

@Composable
fun TopBar() {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(text = "Fartlek Run") },
    )
}

@Composable
fun BottomBar(pageKey: Page, setPageKey: (Page) -> Unit) {
    val icons = mapOf(
        (Page.Run to Icons.Filled.Home),
        (Page.Logs to Icons.Filled.DateRange),
        (Page.Settings to Icons.Filled.Settings)
    )
    BottomAppBar {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (page in Page.entries) {
                IconButton(
                    onClick = { setPageKey(page) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        icons[page]!!,
                        contentDescription = page.name,
                        tint = if (page == pageKey) MaterialTheme.colorScheme.primary else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    FartlekTheme {
        App()
    }
}