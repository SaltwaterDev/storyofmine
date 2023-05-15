package com.example.app.android.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.example.app.android.ui.write.EditHistoryScreen
import com.example.app.android.ui.write.WritingScreen
import com.example.app.android.viewmodel.EditHistoryViewModel
import com.example.app.android.viewmodel.WritingViewModel
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnrememberedGetBackStackEntry")
@ExperimentalAnimatedInsets
@ExperimentalAnimationApi
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class, ExperimentalMaterialApi::class
)
fun NavGraphBuilder.writeGraph(
    navController: NavHostController,
    navToStories: () -> Unit,
) {

    navigation(
        route = UnloneBottomDestinations.Write.name,
        startDestination = UnloneBottomDestinations.Write.routeWithArgs,
    ) {

        composable(
            route = UnloneBottomDestinations.Write.routeWithArgs,
            arguments = UnloneBottomDestinations.Write.arguments
        ) {

            val viewModelStoreOwner = remember { navController.getBackStackEntry("main") }
            val viewModel =
                koinViewModel<WritingViewModel>(viewModelStoreOwner = viewModelStoreOwner)

            WritingScreen(
                viewModel = viewModel,
                draftIdArg = it.arguments?.getString(OptionalDraftArg),
                versionArg = it.arguments?.getString(OptionalVersionArg),
                navToEditHistory = { id -> navToEditHistory(navController, id) },
                navToSignIn = {  },
                onPostSucceed = { navToStories() },
            )
        }

        composable(
            EditDraftHistory.routeWithArgs,
            arguments = EditDraftHistory.arguments
        ) {
            val draftId = it.arguments?.getString(EditDraftHistory.draftArg)
            val viewModel = koinViewModel<EditHistoryViewModel>()
            EditHistoryScreen(draftId,
                viewModel,
                { version -> navToWrite(navController, draftId, version) },
                { navController.popBackStack() })
        }
    }
}

fun navToEditHistory(navController: NavHostController, id: String) {
    navController.navigate("${EditDraftHistory.route}/$id")
}

fun navToWrite(
    navController: NavHostController,
    draftId: String? = null,
    version: String? = null,
) {
    if (draftId != null && version != null) navController.navigate("${UnloneBottomDestinations.Write.route}?${OptionalDraftArg}=${draftId}&${OptionalVersionArg}=${version}")
    else navController.navigate(UnloneBottomDestinations.Write.route)
}