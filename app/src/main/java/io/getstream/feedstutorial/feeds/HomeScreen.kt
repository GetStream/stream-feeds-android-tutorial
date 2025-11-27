package io.getstream.feedstutorial.feeds

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: FeedsViewModel = viewModel(LocalActivity.current as ComponentActivity)

    Surface {
    }
}
