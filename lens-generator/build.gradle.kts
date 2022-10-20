import java.net.URI

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
}

val kspVersion: String by project
val poetVersion: String by project

group = "io.github.mr0xf00"
version = "0.1.0"

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

val emptyJavadocJar = tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
}

fun extProperty(key: String): String {
    return (extra.properties[key] ?: error("property $key not found in extras")) as String
}

val repoUrl : URI get() {
    val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
    val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    return if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
}

publishing {
    publications {
        repositories {
            maven {
                name = "oss"
                url = repoUrl
                credentials {
                    username = extProperty("ossrh.user")
                    password = extProperty("ossrh.pass")
                }
            }
        }
    }
    publications {
        withType<MavenPublication> {
            artifact(emptyJavadocJar)
            pom {
                name.set("LensGenerator")
                description.set("Annotation based lens generation for Kotlin classes")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                url.set("https://github.com/mr0xf00/lensgenerator")
                scm {
                    connection.set("https://github.com/mr0xf00/lensgenerator.git")
                    url.set("https://github.com/mr0xf00/lensgenerator")
                }
                developers {
                    developer {
                        name.set("mr0xf00")
                        email.set("mr0xf00@proton.me")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}