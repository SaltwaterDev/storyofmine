plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = ConfigData.compileSdkVersion
    defaultConfig {
        applicationId = "com.unlone.app.android"
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName
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
        create("staging") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".debugStaging"
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(Deps.constraintLayout)
    implementation(Deps.materialDesign)
    implementation(Deps.appCompat)
    implementation(Deps.activity)
    implementation("androidx.core:core-ktx:1.9.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.composeLifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.composeLifecycle}")

    with(Compose) {
        implementation(ui)
        implementation(preview)
        implementation(material)
        implementation(activity)
        implementation(navigation)
        implementation(viewModel)
        implementation(materialIcon)
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
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Versions.compose}")

    with(Ktx) {
        implementation(core)
        with(Ktx.Coroutine){
            implementation(core)
            implementation(android)
            implementation(playService)
        }
    }

    // log
    implementation(Deps.timber)

    // compose markdown
//    implementation("com.halilibo.compose-richtext:richtext-commonmark:${Versions.richText}")
    implementation("com.github.jeziellago:compose-markdown:0.3.0")

    with(Koin) {
        implementation(core)
        implementation(android)
        implementation(navGraph)
        implementation(compose)
    }
}
