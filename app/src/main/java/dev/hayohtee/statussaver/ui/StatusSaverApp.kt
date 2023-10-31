package dev.hayohtee.statussaver.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.hayohtee.statussaver.StatusDocumentContract
import dev.hayohtee.statussaver.ui.screen.HomeScreen
import dev.hayohtee.statussaver.ui.screen.StatusViewModel
import dev.hayohtee.statussaver.ui.screen.StatusViewerScreen

@Composable
fun StatusSaverApp(modifier: Modifier = Modifier) {
    val viewModel = viewModel<StatusViewModel>(factory = StatusViewModel.FACTORY)
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val directoryLauncher = rememberLauncherForActivityResult(
        contract = StatusDocumentContract(),
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.saveStatusDirectoryUri(uri)
        }
    }

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(
                uiState = uiState,
                onAccessDirectoryClick = { directoryLauncher.launch(null) },
                updateSavedStatus = viewModel::updateSavedStatus,
                onSaveStatusClick = viewModel::saveStatus,
                fetchRecentStatuses = viewModel::fetchRecentStatuses,
                fetchSavedStatuses = viewModel::fetchSavedStatuses,
                onStatusClick = { status ->
                    navController.navigate("view/${Uri.encode(status.uri.toString())}")
                },
                modifier = modifier
            )
        }

        composable(
            route = "view/{uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uri = Uri.parse(backStackEntry.arguments?.getString("uri"))
            StatusViewerScreen(
                statusUri = uri,
                navigateBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }


}


