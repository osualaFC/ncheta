plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.skie).apply(false)
    alias(libs.plugins.sqldelight).apply(false)
    id("com.google.gms.google-services") version "4.4.3" apply false
}
