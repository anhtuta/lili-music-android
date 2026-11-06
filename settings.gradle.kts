pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "lili-music-android"

include(":app")
include(":core:common")
include(":core:model")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:data")
include(":core:playback")
include(":core:lyrics")
include(":core:ui")
include(":feature:library")
include(":feature:player")
include(":feature:lyrics")
include(":feature:settings")
include(":feature:downloads")
