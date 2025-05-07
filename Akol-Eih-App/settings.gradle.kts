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
        maven { url = uri("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    // Prioritize mavenCentral for Lottie to avoid JitPack conflicts
    repositories {
        mavenCentral() {
            content {
                includeModule("com.airbnb.lottie", "lottie")
            }
        }
    }
}

rootProject.name = "AkolEih"
include(":app")