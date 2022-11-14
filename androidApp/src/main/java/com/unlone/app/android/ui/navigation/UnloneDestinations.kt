package com.unlone.app.android.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.unlone.app.android.R
import com.unlone.app.android.ui.UnloneApp

/**
 * Destinations used in the [UnloneApp].
 */
enum class UnloneBottomDestinations(val icon: Int, val label: String?) : UnloneDestination {
    Write(icon = R.drawable.ic_write, label = "write") {
        override val route: String = "write"
        override val routeWithArgs: String = route
        override val arguments: List<NamedNavArgument> = emptyList()
    },
    Stories(icon = R.drawable.ic_book, label = "stories") {
        override val route: String = "stories"
        val optionalRequestedStoryIdArg = "requestedStoryId"
        private val typeArg =
            "?${optionalRequestedStoryIdArg}={${optionalRequestedStoryIdArg}}"

        override val routeWithArgs = "$route$typeArg"
        override val arguments = listOf(navArgument("requestedStoryId") {
            type = NavType.StringType
            nullable = true
        })

    },
    Profile(icon = R.drawable.ic_profile, label = "profiles") {
        override val route: String = "profiles"
        override val routeWithArgs: String = route
        override val arguments: List<NamedNavArgument> = emptyList()
    };
}

interface UnloneDestination {
    val route: String
    val routeWithArgs: String
    val arguments: List<NamedNavArgument>
}


object Drafting : UnloneDestination {
    override val route = "${UnloneBottomDestinations.Write.route}/draft"
    const val optionalDraftArg = "draftId"
    const val optionalVersionArg = "version"
    private const val typeArg =
        "?$optionalDraftArg={$optionalDraftArg}&$optionalVersionArg={$optionalVersionArg}"
    override val routeWithArgs = "$route$typeArg"
    override val arguments = listOf(
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
    override val routeWithArgs = "$route/{$draftArg}"
    override val arguments = listOf(navArgument(draftArg) { type = NavType.StringType })
}


// stories
object StoryDetail : UnloneDestination {
    override val route = "storyDetail"
    const val storyArg = "storyId"
    override val routeWithArgs = "$route/{$storyArg}"
    override val arguments = listOf(navArgument(storyArg) { type = NavType.StringType })
}

object TopicDetail : UnloneDestination {
    override val route = "topicDetail"
    const val topicArg = "topic"
    override val routeWithArgs = "$route/{$topicArg}"
    override val arguments = listOf(navArgument(topicArg) { type = NavType.StringType })
}

object Report : UnloneDestination {
    override val route = "report"
    const val reportTypeArg = "type"
    const val reportIdArg = "reportId"
    override val routeWithArgs = "$route/{$reportTypeArg}/{$reportIdArg}"
    override val arguments = listOf(
        navArgument(reportTypeArg) { type = NavType.StringType },
        navArgument(reportIdArg) { type = NavType.StringType },
    )
}

enum class ReportType {
    story,
    comment
}


// profiles

object Rules : UnloneDestination {
    override val route = "rules"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = emptyList()
}


object Settings : UnloneDestination {
    override val route = "settings"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = listOf()
}


object MyStories : UnloneDestination {
    override val route = "myStories"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = listOf()
}


object SavedStories : UnloneDestination {
    override val route = "saved"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = listOf()
}


