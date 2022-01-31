pluginManagement {
    plugins {
        id("com.google.devtools.ksp") version "1.5.30-1.0.0"
        kotlin("jvm") version "1.5.30"
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":kotlin", ":testCommon", ":processorTest", ":processor", ":annotation", ":android", ":app")
rootProject.name = "KtRssReader"