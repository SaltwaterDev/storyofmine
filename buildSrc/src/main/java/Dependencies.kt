/**
 * To define plugins
 */
object BuildPlugins {
    val android by lazy { "com.android.tools.build:gradle:${Versions.gradlePlugin}" }
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
}

/**
 * To define dependencies
 */
object Deps {
    val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }
    val activity by lazy { "androidx.activity:activity-ktx:${Versions.activity}" }
    val timber by lazy { "com.jakewharton.timber:timber:${Versions.timber}" }
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}" }
    val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}" }
    val junit by lazy { "junit:junit:${Versions.jUnit}" }
    val materialDesign by lazy { "com.google.android.material:material:${Versions.materialDesign}" }
    const val multiplatformPaging = "io.github.kuuuurt:multiplatform-paging:${Versions.kmmPaging}"
    const val realm = "io.realm.kotlin:library-base:${Versions.realm}"

}

object Ktx{
    const val core = "androidx.core:core-ktx:1.9.0"
    const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"

    object Coroutine{
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.ktxCoroutine}"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.ktxCoroutine}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.ktxCoroutine}"
        const val playService = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.ktxCoroutine}"
    }
}


object Compose {
    const val compiler = "androidx.compose.compiler:compiler:${Versions.composeCompiler}"
    const val ui = "androidx.compose.ui:ui:${Versions.compose}"
    const val runtime = "androidx.compose.runtime:runtime:${Versions.compose}"
    const val activity = "androidx.activity:activity-compose:${Versions.compose}"
    const val uiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
    const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
    const val material = "androidx.compose.material:material:${Versions.compose}"
    const val materialIconsExtended = "androidx.compose.material:material-icons-extended:${Versions.compose}"
    const val navigation = "androidx.navigation:navigation-compose:${Versions.navCompose}"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.composeLifecycle}"
    const val preview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose}"
    const val materialIcon = "androidx.compose.material:material-icons-extended:${Versions.compose}"
    const val composePaging = "androidx.paging:paging-compose:${Versions.pagingCompose}"

}

object Koin {
    const val core = "io.insert-koin:koin-core:${Versions.koin}"
    const val test = "io.insert-koin:koin-test:${Versions.koin}"
    const val android = "io.insert-koin:koin-android:${Versions.koin}"
    const val navGraph = "io.insert-koin:koin-androidx-navigation:${Versions.koin}"
    const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
}

object Accompanist {
    const val systemUiController = "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"
    const val insets = "com.google.accompanist:accompanist-insets:${Versions.accompanist}"
    const val insetsUi = "com.google.accompanist:accompanist-insets-ui:${Versions.accompanist}"
    const val placeholder = "com.google.accompanist:accompanist-placeholder-material:${Versions.accompanist}"
    const val navAnimation = "com.google.accompanist:accompanist-navigation-animation:${Versions.accompanist}"
    const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:${Versions.accompanist}"
}

object Ktor{
    const val clientCore = "io.ktor:ktor-client-core:${Versions.ktorVersion}"
    const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:${Versions.ktorVersion}"
    const val serialization = "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktorVersion}"
    const val clientMock = "io.ktor:ktor-client-mock:${Versions.ktorVersion}"
    const val okHttp = "io.ktor:ktor-client-okhttp:${Versions.ktorVersion}"
    const val darwin = "io.ktor:ktor-client-darwin:${Versions.ktorVersion}"
    const val encoding = "io.ktor:ktor-client-encoding:${Versions.ktorVersion}"
}