plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

val kspVersion: String by project
val poetVersion: String by project

kotlin {
    explicitApi()
    jvm()
    js {
        browser()
        nodejs()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("com.squareup:kotlinpoet:$poetVersion")
                implementation("com.squareup:kotlinpoet-ksp:$poetVersion")
                implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
            }
        }
    }
}
