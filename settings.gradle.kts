pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    this.repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TeamScore_Rev4"
include(":app")
 