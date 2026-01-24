import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.sspl.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.sspl.sosapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 7
        versionName = "1.0.7"
    }
    signingConfigs {
        create("sspl") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
        }
        create("bhimsoft") {
            storeFile = file(keystoreProperties["bhimsoft_storeFile"] as String)
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
        }
        create("stageRelease") {
            storeFile = file("../keystore/stagerelease.keystore")
            storePassword = "stagerelease"
            keyAlias = "stagerelease"
            keyPassword = "stagerelease"
        }
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("stage") {
            dimension = "version"
            signingConfig = signingConfigs.getByName("stageRelease")
        }
        create("sspl") {
            versionCode = 7
            versionName = "1.0.7"
            dimension = "version"
            signingConfig = signingConfigs.getByName("sspl")
        }
        create("bhimsoft") {
            versionCode = 2
            versionName = "1.0.2"
            dimension = "version"
            signingConfig = signingConfigs.getByName("bhimsoft")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.core.splash)
    //Di
    implementation(libs.koin.core)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.android)

    implementation(project.dependencies.platform(libs.firebase.bom)) // Use the latest version
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    
    // Pusher for real-time notifications
    implementation(libs.pusher.java.client)
}