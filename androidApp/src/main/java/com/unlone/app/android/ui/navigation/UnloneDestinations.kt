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


// stories
object StoryDetail : UnloneDestination {
    override val route = "storyDetail"
    const val storyArg = "storyId"
    val routeWithArgs = "$route/{$storyArg}"
    val arguments = listOf(navArgument(storyArg) { type = NavType.StringType })
}

object TopicDetail : UnloneDestination {
    override val route = "topicDetail"
    const val topicArg = "topic"
    val routeWithArgs = "$route/{$topicArg}"
    val arguments = listOf(navArgument(topicArg) { type = NavType.StringType })
}

object Report : UnloneDestination {
    override val route = "report"
    const val reportTypeArg = "type"
    const val reportIdArg = "reportId"
    val routeWithArgs = "$route/{$reportTypeArg}/{$reportIdArg}"
    val arguments = listOf(
        navArgument(reportTypeArg) { type = NavType.StringType },
        navArgument(reportIdArg) { type = NavType.StringType },
    )
}

enum class ReportType{
    story,
    comment
}



// profiles

object Rules : UnloneDestination {
    override val route = "rules"
//    val routeWithArgs = "$route"
//    val arguments = listOf()
}


object Settings : UnloneDestination {
    override val route = "settings"
//    val routeWithArgs = "$route"
//    val arguments = listOf()
}


object MyStories : UnloneDestination {
    override val route = "myStories"
//    val routeWithArgs = "$route"
//    val arguments = listOf()
}


object SavedStories : UnloneDestination {
    override val route = "saved"
//    val routeWithArgs = "$route"
//    val arguments = listOf()
}


