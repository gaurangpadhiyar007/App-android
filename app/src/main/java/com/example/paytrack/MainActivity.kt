
package com.example.paytrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.paytrack.ui.AddRecordScreen
import com.example.paytrack.ui.ListScreen
import com.example.paytrack.ui.ReportScreen
import com.example.paytrack.ui.theme.PayTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PayTrackTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val backStack by navController.currentBackStackEntryAsState()
                            val route = backStack?.destination?.route
                            listOf(
                                "add" to "Add",
                                "list" to "List",
                                "reports" to "Reports"
                            ).forEach { (r, label) ->
                                NavigationBarItem(
                                    selected = route == r,
                                    onClick = { navController.navigate(r) },
                                    icon = { Icon(Icons.DefaultPlaceholder, contentDescription = label) },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    NavHost(navController, startDestination = "add", modifier = Modifier.padding(padding)) {
                        composable("add") { AddRecordScreen() }
                        composable("list") { ListScreen() }
                        composable("reports") { ReportScreen() }
                    }
                }
            }
        }
    }
}

// simple placeholder icons without extra deps
object Icons {
    @Composable
    val DefaultPlaceholder: @Composable () -> Unit
        get() = { Icon(androidx.compose.material.icons.Icons.Default.List, contentDescription = null) }
}
