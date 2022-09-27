plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.7.0"
    id("io.realm.kotlin")
    id("dev.icerock.mobile.multiplatform-resources")
    id("io.kotest.multiplatform") version Versions.kotest
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
        }
    }


    sourceSets {

        val commonMain by getting {
            dependencies {
                with(Ktor){
                    implementation(contentNegotiation)
                    implementation(clientCore)
                    implementation(serialization)
                }
                with(Koin) {
                    api(core)
                    api(test)
                }
                // logger
                implementation(kotlin("stdlib-common"))
                implementation("co.touchlab:kermit:1.1.3")
                // mongodb realm
                implementation(Ktx.Coroutine.core)
                implementation("io.realm.kotlin:library-base:1.0.0")
                // datetime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-framework-engine:${Versions.kotest}")
                implementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
                implementation(Ktx.Coroutine.test)
                implementation(Ktor.clientMock)
                implementation("io.mockk:mockk:1.13.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Ktor.okHttp)
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
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 32
    }
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
    multiplatformResourcesPackage = "org.example.library" // requiredcommonClientMain
    multiplatformResourcesClassName = "SharedRes" // optional, default MR
//    multiplatformResourcesVisibility = dev.icerock.gradle.MRVisibility.Internal // optional, default Public
    iosBaseLocalizationRegion = "en" // optional, default "en"
//    multiplatformResourcesSourceSet = "commonClientMain"  // optional, default "commonMain"
}
