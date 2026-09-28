import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import java.util.Properties

// Release signing credentials live in keystore.properties (git-ignored), not in this file.
// See keystore.properties.template for the expected keys.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { load(it) }
    }
}
val hasReleaseKeystore = keystoreProperties.getProperty("storeFile") != null

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}
//plugins {
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//
//    id("com.google.devtools.ksp")   //vpg 1/18/2024
//}

android {
    namespace = "com.golfpvcc.teamscore_rev4"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.golfpvcc.teamscore_rev4"
        minSdk = 30
        targetSdk = 36
        versionCode = 3
        versionName = "3.00"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val buildTimestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a"))
        buildConfigField("String", "BUILD_TIME", "\"$buildTimestamp\"")
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Falls back to unsigned when keystore.properties is absent, so the
            // project still configures on a fresh clone.
            signingConfig = if (hasReleaseKeystore) signingConfigs.getByName("release") else null
        }
        debug {
            versionNameSuffix = "-debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.10"
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom.v20251000))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.support.annotations)
    implementation(libs.androidx.compose.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
 //   androidTestImplementation(platform(libs.androidx.compose.bom.v20251000))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
//vpg add 3/6/2024
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.runtime.rxjava2)

    // Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler.v250)

    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Extended Icons
    implementation(libs.androidx.compose.material.icons.extended)
    // vpg 8/7/2024
    implementation(libs.androidx.lifecycle.viewmodel.compose)

}