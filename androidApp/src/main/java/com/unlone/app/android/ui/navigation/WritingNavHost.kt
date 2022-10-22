package com.unlone.app.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.unlone.app.android.ui.write.EditHistoryScreen
import com.unlone.app.android.ui.write.WritingScreen
import com.unlone.app.android.viewmodel.EditHistoryViewModel
import com.unlone.app.android.viewmodel.WritingViewModel
import org.koin.androidx.compose.koinViewModel


@ExperimentalAnimatedInsets
@ExperimentalAnimationApi
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class
)
fun NavGraphBuilder.writeGraph(
    navController: NavHostController,
) {

    navigation(
        route = UnloneBottomDestinations.Write.route,
        startDestination = Drafting.routeWithArgs,
    ) {
        composable(
            Drafting.routeWithArgs,
            arguments = Drafting.arguments
        ) {
            val draftId = it.arguments?.getString(Drafting.optionalDraftArg)
            val version = it.arguments?.getString(Drafting.optionalVersionArg)
            val viewModel = koinViewModel<WritingViewModel>()

            WritingScreen(
                viewModel,
                draftId,
                version,
                navToEditHistory = { id -> navToEditHistory(navController, id) },
                navToSignIn = { navigateToAuth(navController, UnloneBottomDestinations.Write.route) },
            )
        }

        composable(
            EditDraftHistory.routeWithArgs,
            arguments = EditDraftHistory.arguments
        ) {
            val draftId = it.arguments?.getString(EditDraftHistory.draftArg)
            val viewModel = koinViewModel<EditHistoryViewModel>()
            EditHistoryScreen(
                draftId,
                viewModel,
                { version -> navToSWrite(navController, draftId, version) },
                { navController.popBackStack() }
            )
        }
    }
}

fun navToEditHistory(navController: NavHostController, id: String) {
    navController.navigate("${EditDraftHistory.route}/$id")
}

fun navToSWrite(
    navController: NavHostController,
    draftId: String? = null,
    version: String? = null,
) {
    if (draftId != null && version != null)
        navController.navigate("${Drafting.route}?${Drafting.optionalDraftArg}=${draftId}&${Drafting.optionalVersionArg}=${version}")
    else navController.navigate(Drafting.route)
}