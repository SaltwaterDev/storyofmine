plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "com.unlone.app.android"
        minSdk = 24
        targetSdk = 32
        versionCode = 20
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // compose
    val composeVersion = "1.2.0-rc03"
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.activity:activity-compose:1.5.0")
    implementation("androidx.navigation:navigation-compose:2.5.0")

    // accompanist
    val accompanistVersion = "0.24.13-rc"
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets-ui:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")


    // testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")


    // ktx
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // log
    implementation("com.jakewharton.timber:timber:5.0.1")

    // koin
    with(Deps.Koin) {
        implementation(core)
        implementation(android)
        implementation(navGraph)
        implementation(compose)
    }
}



object Versions {
    const val koin = "3.2.0-beta-1"
}

object Deps {

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
        const val navGraph = "io.insert-koin:koin-androidx-navigation:${Versions.koin}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
    }

}

