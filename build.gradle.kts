buildscript {
    val kotlinVersion by extra { "1.6.10" }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
        classpath("com.android.tools.build:gradle:8.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    id ("dagger.hilt.android.plugin") version "2.38.1" apply false
    //alias(libs.plugins.google.dagger.hilt.android) apply false
//    id ("com.google.dagger.hilt.android") version "2.38.1" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}