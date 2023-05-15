plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

android {
    compileSdk = ConfigData.compileSdkVersion
    defaultConfig {
        applicationId = "com.example.app.android"
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
    namespace = "com.example.app.android"
}

dependencies {
    implementation(project(":shared"))
    implementation(Deps.constraintLayout)
    implementation(Deps.materialDesign)
    implementation(Deps.appCompat)
    implementation(Deps.activity)

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.composeLifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.composeLifecycle}")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.4.3")
    implementation("androidx.startup:startup-runtime:1.1.1")
    implementation("androidx.core:core-ktx:1.10.1")

    with(Compose) {
        implementation(ui)
        implementation(preview)
        implementation(material)
        implementation(activity)
        implementation(navigation)
        implementation(viewModel)
        implementation(materialIconsExtended)
        implementation(composePaging)
    }

    with(Accompanist) {
        implementation(systemUiController)
        implementation(insets)
        implementation(insetsUi)
        implementation(placeholder)
        implementation(navAnimation)
        implementation(swipeRefresh)
    }

    // testing
    testImplementation("junit:junit:${Versions.jUnit}")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Versions.compose}")

    with(Ktx) {
        implementation(core)
        implementation(datetime)
        with(Ktx.Coroutine) {
            implementation(core)
            implementation(android)
            implementation(playService)
        }
    }

    // log
    implementation(Deps.timber)

    // compose markdown
    implementation("com.github.jeziellago:compose-markdown:0.3.0")


    with(Koin) {
        implementation(core)
        implementation(android)
        implementation(navGraph)
        implementation(compose)
        testImplementation(unitTest)
    }
}
