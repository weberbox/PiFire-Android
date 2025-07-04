import io.sentry.android.gradle.extensions.InstrumentationFeature
import java.io.FileInputStream
import java.util.EnumSet
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.services)
    alias(libs.plugins.sentry.android.gradle)
    alias(libs.plugins.android.dagger.hilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

private val keystoreProperties = getKeystoreProperties()

val vMajor = 3
val vMinor = 0
val vPatch = 0
val isAlpha = true

android {
    compileSdk = 36

    namespace = "com.weberbox.pifire"

    defaultConfig {
        applicationId = "com.weberbox.pifire"
        minSdk = 24
        targetSdk = 36
        versionCode = vMajor * 1000000 + vMinor * 10000 + vPatch * 100
        versionName = "${vMajor}.${vMinor}.${vPatch}"
    }

    if(keystoreProperties.isNotEmpty()) {
        signingConfigs {
            create("release") {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        release {
            manifestPlaceholders += mapOf()
            manifestPlaceholders.putAll(mapOf("appName" to "PiFire", "environment" to "production"))
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
            buildConfigField("Boolean", "ALPHA_BUILD", isAlpha.toString())
            buildConfigField("String", "BUILD_TIME", "\"${getCurrentTime()}\"")
            buildConfigField("String", "GIT_BRANCH", "\"${getGitBranch()}\"")
            buildConfigField("String", "GIT_REV", "\"${getGitRevCount()}\"")
            if (keystoreProperties.isNotEmpty())
                signingConfig = signingConfigs.getByName("release")
        }
        debug {
            manifestPlaceholders.putAll(mapOf("appName" to "PiFire Debug", "environment" to "debug"))
            applicationIdSuffix = ".debug"
            isDebuggable = true
            buildConfigField("Boolean", "ALPHA_BUILD", isAlpha.toString())
            buildConfigField("String", "BUILD_TIME", "\"${getCurrentTime()}\"")
            buildConfigField("String", "GIT_BRANCH", "\"${getGitBranch()}\"")
            buildConfigField("String", "GIT_REV", "\"${getGitRevCount()}\"")
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("dev") {
            dimension = "version"
        }
        create("github") {
            dimension = "version"
        }
        create("playstore") {
            dimension = "version"
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kapt {
        correctErrorTypes = true
    }

    applicationVariants.configureEach {
        val variant = this
        val appName = rootProject.name.lowercase()
        val flavor = variant.flavorName
        val buildType = variant.buildType.name
        val versionName = variant.versionName
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "${appName}-${flavor}-${buildType}-${versionName}.apk"
                output.outputFileName = outputFileName
            }
    }
}

dependencies {
    kapt(libs.google.dagger.hilt.complier)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3.window.size)
    implementation(libs.androidx.compose.material3.android)
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uitooling)
    implementation(libs.androidx.compose.uitoolingpreview)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.compose.fonts)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.crypto)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.sentry.android)
    implementation(libs.sentry.android.timber)
    implementation(libs.google.dagger.hilt)
    implementation(libs.google.play.scanner)
    implementation(libs.google.play.update)
    implementation(libs.composables.core)
    implementation(libs.haze.android)
    implementation(libs.haze.materials)
    implementation(libs.timber.android)
    implementation(libs.tapadoo.alerter)
    implementation(libs.compose.ratingbar)
    implementation(libs.compose.preference)
    implementation(libs.compose.lottie)
    implementation(libs.compose.coil)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.kotlinx)
    implementation(libs.zoomable)
    implementation(libs.onesignal)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)
    implementation(libs.socket.io) {
        exclude(group = "org.json", module = "json")
    }
    coreLibraryDesugaring(libs.desugar)
    debugImplementation(libs.squareup.leakcanary)

}

sentry {
    tracingInstrumentation {
        enabled.set(true)
        features.set(EnumSet.allOf(InstrumentationFeature::class.java) - InstrumentationFeature.OKHTTP)
    }
    ignoredBuildTypes.set(setOf("release"))
}

fun getKeystoreProperties(): Properties {
    val propertiesFile: String? by project
    val keystoreProperties = Properties()
    propertiesFile?.let { file ->
        val keystoreFile = File(file)
        if (keystoreFile.exists()) {
            keystoreProperties.load(FileInputStream(keystoreFile))
        }
    }
    return keystoreProperties
}

fun getCurrentTime(): String {
    return System.currentTimeMillis().toString()
}

fun getGitRevCount(): String {
    val process = ProcessBuilder("git", "rev-list", "--count", "HEAD")
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().use { it.readText() }.trim()
    process.waitFor()
    return output
}

fun getGitBranch(): String {
    val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().use { it.readText() }.trim()
    process.waitFor()
    return output
}