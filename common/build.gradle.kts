import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvm()

    @Suppress("UnstableApiUsage")
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    dependencies {
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.cio)
        implementation(libs.kotlinx.io)
        implementation("org.apache.commons:commons-compress:1.28.0")
    }
}

