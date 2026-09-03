// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    id("com.android.application") version "8.13.0" apply false
//
//    id("org.jetbrains.kotlin.android") version "2.2.20" apply false
//    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false  //vpg 3/6/2024
//}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.3.6" apply false
}