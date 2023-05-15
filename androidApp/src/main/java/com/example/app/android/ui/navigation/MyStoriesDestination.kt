package com.example.app.android.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.app.android.R
import com.example.app.android.ui.MyStoriesApp

/**
 * Destinations used in the [MyStoriesApp].
 */


const val OptionalDraftArg = "draftId"
const val OptionalVersionArg = "version"


interface MyStoriesDestination {
    val route: String
    val routeWithArgs: String
    val arguments: List<NamedNavArgument>
}


enum class MyStoriesBottomDestinations(val icon: Int, val label: String?) : MyStoriesDestination {
    Write(icon = R.drawable.ic_write, label = "write") {
        override val route: String = "writeRoute"

        val optionalDraftArg = OptionalDraftArg
        val optionalVersionArg = OptionalVersionArg
        private val typeArg =
            "?$optionalDraftArg={$optionalDraftArg}&$optionalVersionArg={$optionalVersionArg}"
        override val routeWithArgs = "$route$typeArg"

        override val arguments: List<NamedNavArgument> = listOf(
            navArgument(optionalDraftArg) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(optionalVersionArg) {
                type = NavType.StringType
                nullable = true
            }
        )
    },
}


object EditDraftHistory : MyStoriesDestination {
    override val route = "editHistory"
    const val draftArg = "draftId"
    override val routeWithArgs = "$route/{$draftArg}"
    override val arguments = listOf(navArgument(draftArg) { type = NavType.StringType })
}