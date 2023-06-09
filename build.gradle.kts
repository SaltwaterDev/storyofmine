buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("com.google.gms:google-services:4.3.14")
        classpath("io.realm.kotlin:gradle-plugin:${Versions.realm}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
        classpath("dev.icerock.moko:resources-generator:0.20.1")
        // build kconfig
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.13.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
