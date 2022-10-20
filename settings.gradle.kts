pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        val kspVersion : String by settings
        val kotlinVersion : String by settings
        kotlin("multiplatform") version kotlinVersion
        id("com.google.devtools.ksp") version kspVersion
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "LensGenerator"
include (":sample", "lens-generator")
