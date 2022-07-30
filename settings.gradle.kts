pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "unlone-kmm"
include(":androidApp")
include(":shared")