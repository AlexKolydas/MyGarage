package com.alexkolydas.mygarage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexkolydas.mygarage.core.ui.theme.MyGarageTheme
import com.alexkolydas.mygarage.feature.detail.DetailScreen
import com.alexkolydas.mygarage.feature.garage.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGarageTheme {
                val navController = rememberNavController()
                NavHost(
                    navController    = navController,
                    startDestination = "home",
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                ) {
                    composable("home") {
                        HomeScreen(
                            onNavigateToDetail = { id ->
                                navController.navigate("detail/$id")
                            },
                        )
                    }
                    composable("detail/{vehicleId}") { entry ->
                        val vehicleId = entry.arguments
                            ?.getString("vehicleId")?.toLongOrNull()
                            ?: return@composable
                        DetailScreen(
                            vehicleId = vehicleId,
                            onBack    = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
