import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.7.20"
    id("io.realm.kotlin")
    id("dev.icerock.mobile.multiplatform-resources")
    id("io.kotest.multiplatform") version Versions.kotest
    id("com.codingfeline.buildkonfig")
    id("com.rickclephas.kmp.nativecoroutines") version "0.13.1"
}

version = "1.0"

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            export("io.github.kuuuurt:multiplatform-paging:${Versions.kmmPaging}")
        }
    }


    sourceSets {

        val commonMain by getting {
            dependencies {
                with(Ktor){
                    implementation(contentNegotiation)
                    implementation(clientCore)
                    implementation(serialization)
                    implementation(encoding)
                }
                with(Koin) {
                    api(core)
                    api(test)
                }
                // logger
                implementation(kotlin("stdlib-common"))
                implementation("co.touchlab:kermit:1.1.3")
                implementation("co.touchlab:kermit-crashlytics:1.1.3")
                // mongodb realm
                implementation(Ktx.Coroutine.core)
                implementation(Deps.realm)
                // datetime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                // pagination
                api(Deps.multiplatformPaging)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-framework-engine:${Versions.kotest}")
                implementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
                implementation(Ktx.Coroutine.test)
                implementation(Ktor.clientMock)
//                implementation("io.mockk:mockk:1.13.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Ktor.okHttp)
                implementation("androidx.startup:startup-runtime:1.1.1")
                // security
                implementation("androidx.security:security-crypto:1.0.0")

            }
        }
        val androidTest by getting {
            dependencies {
                implementation("androidx.security:security-app-authenticator:1.0.0-alpha02")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Ktor.darwin)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }

    // export correct artifact to use all classes of moko-resources directly from Swift
    targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).all {
        binaries.withType(org.jetbrains.kotlin.gradle.plugin.mpp.Framework::class.java).all {
            export("dev.icerock.moko:resources:0.20.1")
        }
    }
}

android {
    compileSdk = ConfigData.compileSdkVersion
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion
    }
    namespace = "com.unlone.app"
}


dependencies {
    // locale resources
    "commonMainApi"("dev.icerock.moko:resources:0.20.1")
    "androidMainApi"("dev.icerock.moko:resources-compose:0.20.1")
    "commonTestImplementation"("dev.icerock.moko:resources-test:0.20.1")
    // testing
//    "commonTestImplementation"("io.mockk:mockk-common:1.13.1")
//    "testImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}


multiplatformResources {
    multiplatformResourcesPackage = "org.example.library" // requiredcommonClientMainViperphD
    multiplatformResourcesClassName = "SharedRes" // optional, default MR
//    multiplatformResourcesVisibility = dev.icerock.gradle.MRVisibility.Internal // optional, default Public
    iosBaseLocalizationRegion = "en" // optional, default "en"
//    multiplatformResourcesSourceSet = "commonClientMain"  // optional, default "commonMain"
}

buildkonfig {
    packageName = "com.unlone.app"
     objectName = "UnloneConfig"
//     exposeObjectWithName = "YourAwesomePublicConfig"

    defaultConfigs {
        buildConfigField(STRING, "baseUrl", "https://unlone-dev-dot-unlone.an.r.appspot.com")
    }
    // flavor is passed as a first argument of defaultConfigs
    defaultConfigs("staging") {
        buildConfigField(STRING, "baseUrl", "https://unlone-staging-dot-unlone.an.r.appspot.com/")
    }

    // flavor is passed as a first argument of defaultConfigs
    defaultConfigs("prod") {
        buildConfigField(STRING, "baseUrl", "https://unlone.an.r.appspot.com")
    }
}
