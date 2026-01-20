import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.security.crypto)
            implementation(libs.accompanist.permissions)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.messaging)
            //Pusher
            //implementation("com.pusher:pusher-java-client:2.2.6")

        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            //implementation("io.github.aakira:napier:1.4.1")
            //Pusher
            //implementation("io.ktor:ktor-client-websockets:2.3.5")
        }
        val commonMain by getting {
            resources.srcDirs("src/commonMain/resources")
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.lifecycle.runtime.compose)

                // peekaboo-image-picker
                implementation(libs.peekaboo.image.picker)
                // Geolocation
                implementation(libs.compass.geolocation)
                implementation(libs.compass.geolocation.mobile)
                // Location permissions for mobile
                implementation(libs.compass.permissions.mobile)

                //Navigation
                implementation(libs.navigation.compose)
                //Koin
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.composeVM)
                implementation(libs.koin.test)

                //Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.resources)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.napier)



                //implementation(libs.multiplatform.settings.encryption)
                //Retrofit + OkHttp
//            implementation(libs.retrofit)
//            implementation(project.dependencies.platform(libs.okhttp.bom))
//            implementation(libs.okhttp)
//            implementation(libs.logging.interceptor)
//            implementation(libs.retrofit2.kotlinx.serialization.converter)
                implementation(libs.kotlinx.serialization.json)
                //Coil
                implementation(libs.coil.compose)
                implementation(libs.coil.compose.core)
                implementation(libs.coil.network.ktor)

                implementation(libs.kotlinx.datetime)
//                implementation(libs.kotlinx.datetime.i18n)
//                implementation(libs.annotations.common)
//                implementation(libs.androidx.core.i18n)
//                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")


//            implementation(compose.components.resources)
//            implementation(compose.components.uiToolingPreview)
//
//            implementation(libs.androidx.lifecycle.viewmodel)
//            implementation(libs.androidx.lifecycle.runtime.compose)
//            implementation(libs.navigation.compose)
//            implementation(libs.libphonenumber)


                //implementation(libs.compose.ui.tooling)
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.sspl"
    compileSdk = 35

//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//    sourceSets["main"].res.srcDirs("src/androidMain/res")

    //sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
compose.resources {
    publicResClass = true
    packageOfResClass = "com.sspl.resources"
    generateResClass = always
}
