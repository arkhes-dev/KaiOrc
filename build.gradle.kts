plugins {
    kotlin("jvm") version "2.0.21"
    id("com.vanniktech.maven.publish") version "0.29.0"
    signing
}

// Must exactly match the arkhes-dev GitHub org login for Sonatype's namespace verification -
// Maven group IDs allow hyphens even though the Kotlin package (io.github.arkhesdev.kaiorc) can't.
group = "io.github.arkhes-dev"
version = "0.1.0"
description = "A lightweight Kotlin library for AI workflow orchestration, provider abstraction, prompt pipelines, and structured execution."

// Pure Kotlin/JVM — no Android, no Compose, no Hilt/KSP dependency. Consumers wire kaiorc's
// @Inject-annotated classes into their own DI graph; kaiorc itself never depends on a specific
// DI framework or platform.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        allWarningsAsErrors.set(true)
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("javax.inject:javax.inject:1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}

tasks.test {
    useJUnit()
}

mavenPublishing {
    publishToMavenCentral()

    coordinates(group.toString(), "kaiorc", version.toString())

    pom {
        name.set("KaiOrc")
        description.set(project.description)
        url.set("https://github.com/arkhes-dev/KaiOrc")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }
        developers {
            developer {
                id.set("arkhes-dev")
                name.set("Arkhes")
                email.set("rakeshganapathy.dev@gmail.com")
                organization.set("Arkhes")
                organizationUrl.set("https://github.com/arkhes-dev")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/arkhes-dev/KaiOrc.git")
            developerConnection.set("scm:git:ssh://github.com/arkhes-dev/KaiOrc.git")
            url.set("https://github.com/arkhes-dev/KaiOrc")
        }
    }
}

// Signs via the local `gpg` command (key stays in the GPG keyring, never exported to a file).
// Passphrase/key name come from ~/.gradle/gradle.properties (signing.gnupg.*), not this file.
signing {
    useGpgCmd()
    sign(publishing.publications)
}
