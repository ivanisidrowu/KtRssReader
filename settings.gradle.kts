pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

include(
    ":kotlin",
    ":testCommon",
    ":processorTest",
    ":processor",
    ":annotation",
    ":android",
    ":sample"
)
rootProject.name = "KtRssReader"