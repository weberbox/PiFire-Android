// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.services) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.android.ksp) apply false
    alias(libs.plugins.android.dagger.hilt) apply false
    alias(libs.plugins.spotless)
}

// Formatting: `./gradlew spotlessCheck` / `spotlessApply`. ktlint reads its code style and rules from the root
// .editorconfig, shared with Android Studio. The Gradle daemon caches it: run `./gradlew --stop` after editing it.
spotless {
    kotlin {
        target("app/src/**/*.kt")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
    }
    kotlinGradle {
        target("*.gradle.kts", "app/*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
    }
}
