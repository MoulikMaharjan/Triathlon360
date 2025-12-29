pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // 🔥 REQUIRED for MPAndroidChart
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Triathlon360"
include(":app")
