package com.lingyunchi.fartlek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.lingyunchi.fartlek.context.LocalNavControllerProvider
import com.lingyunchi.fartlek.ui.theme.FartlekTheme
import com.lingyunchi.fartlek.views.ConfigEdit
import com.lingyunchi.fartlek.views.MainView
import com.lingyunchi.fartlek.views.RunFinished
import com.lingyunchi.fartlek.views.Running
import io.paperdb.Paper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init Paper DB
        Paper.init(this)

        enableEdgeToEdge()
        setContent {
            FartlekTheme {
                App()
            }

        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()

    LocalNavControllerProvider(navController) {
        NavHost(navController = navController, startDestination = Main) {
            composable<Main> {
                MainView()
            }
            composable<ConfigEdie> { navBackStackEntry ->
                val configId = navBackStackEntry.toRoute<ConfigEdie>().id
                ConfigEdit(configId)
            }
            composable<Running> {
                Running()
            }
            composable<RunFinished> { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<RunFinished>()
                RunFinished(args.startTime, args.duration, args.configId)
            }
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