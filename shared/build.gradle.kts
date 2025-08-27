import co.touchlab.skie.configuration.FlowInterop
import co.touchlab.skie.configuration.SealedInterop
import co.touchlab.skie.configuration.SuspendInterop
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.skie)
    alias(libs.plugins.sqldelight)
}

dependencies {
    implementation(platform(libs.koin.bom))
}

skie {
    features {
        coroutinesInterop.set(true)
        group {
            FlowInterop.Enabled(true)
            SuspendInterop.Enabled(true)
            SealedInterop.Enabled(true)
        }
    }
}

sqldelight {
    databases {
        create("NchetaDatabase") {
            packageName.set("com.fredrickosuala.ncheta.database")
        }
    }
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
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
            // Tells the KMP linker to link against the Firebase frameworks provided by SPM
            linkerOpts.add("-framework")
            linkerOpts.add("FirebaseAuth")
        }
    }

    sourceSets {

        named { it.lowercase().startsWith("ios") }.configureEach {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.core)
            api(libs.generativeai.google)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.lifecycle.viewmodel.compose)
            api(libs.koin.core)
            implementation(libs.firebase.auth)
            implementation("dev.gitlive:firebase-firestore:1.13.0")
            implementation(libs.multiplatformSettings.noArg)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.purchases.core)
            implementation(libs.purchases.datetime)
//            implementation(libs.purchases.either)
//            implementation(libs.purchases.result)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
            implementation(libs.koin.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        androidUnitTest.dependencies {
            implementation(libs.mockk)
            implementation(libs.junit)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.fredrickosuala.ncheta"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
