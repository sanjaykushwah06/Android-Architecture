/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Main activity hosting navigation and Compose UI setup
 */

package com.iiwa

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.iiwa.navigation.NavGraph
import com.iiwa.ui.theme.IwaaAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IwaaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyAppNav()
                }
            }
        }
    }
}

@Composable
fun MyAppNav() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
