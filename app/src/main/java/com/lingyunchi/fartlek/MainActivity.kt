package com.lingyunchi.fartlek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.lingyunchi.fartlek.contexts.ThemeContext
import com.lingyunchi.fartlek.contexts.ThemeContextInfo
import com.lingyunchi.fartlek.ui.theme.FartlekTheme
import com.lingyunchi.fartlek.views.MainView
import io.paperdb.Paper

class MainActivity : ComponentActivity() {
    private var darkTheme by mutableStateOf(DarkTheme.System)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init Paper DB
        Paper.init(this)
        val darkThemeSaved = Paper.book("setting").read("dark-theme", DarkTheme.Dark)!!
        darkTheme = darkThemeSaved

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                ThemeContext provides ThemeContextInfo(darkTheme,
                    { darkTheme = it })
            ) {
                FartlekTheme(
                    darkTheme = darkTheme == DarkTheme.Dark ||
                            (darkTheme == DarkTheme.System && isSystemInDarkTheme())
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Main) {
        composable<Main> {
            MainView(navigateTo = { navController.navigate(it) })
        }
    }
}


@Preview(showBackground = true, device = "id:Redmi K30 Pro")
@Composable
fun AppPreview() {
    FartlekTheme {
        App()
    }
}