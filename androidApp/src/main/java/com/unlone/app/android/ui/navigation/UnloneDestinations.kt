package com.unlone.app.android.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.unlone.app.android.R
import com.unlone.app.android.ui.UnloneApp

/**
 * Destinations used in the [UnloneApp].
 */
enum class UnloneBottomDestinations(val icon: Int, val route: String) {
    Write(icon = R.drawable.ic_write, route = "write"),
    Stories(icon = R.drawable.ic_book, route = "stories"),
    Profile(icon = R.drawable.ic_profile, route = "profiles");
}

interface UnloneDestination {
    val route: String
}


object Drafting : UnloneDestination {
    override val route = "${UnloneBottomDestinations.Write.route}/draft"
    const val optionalDraftArg = "draftId"
    const val optionalVersionArg = "version"
    private const val accountTypeArg =
        "?$optionalDraftArg={$optionalDraftArg}&$optionalVersionArg={$optionalVersionArg}"
    val routeWithArgs = "$route$accountTypeArg"
    val arguments = listOf(
        navArgument(optionalDraftArg) {
            type = NavType.StringType
            nullable = true
        },
        navArgument(optionalVersionArg) {
            type = NavType.StringType
            nullable = true
        }
    )
}

object EditDraftHistory : UnloneDestination {
    override val route = "editHistory"
    const val draftArg = "draftId"
    val routeWithArgs = "$route/{$draftArg}"
    val arguments = listOf(navArgument(draftArg) { type = NavType.StringType })
}


