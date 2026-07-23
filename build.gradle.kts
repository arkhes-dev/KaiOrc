plugins {
    kotlin("jvm") version "2.0.21"
}

group = "dev.arkhes"
version = "0.1.0-SNAPSHOT"
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
