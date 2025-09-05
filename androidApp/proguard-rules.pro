#############################################
# GENERAL KOTLIN / COROUTINES
#############################################
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlin.** { *; }
-keep class kotlin.Metadata { *; }

#############################################
# KOIN (Dependency Injection)
#############################################
# Koin relies on reflection
-keep class org.koin.** { *; }
-dontwarn org.koin.**
-keepclassmembers class * {
    @org.koin.core.annotation.** <fields>;
    @org.koin.core.annotation.** <methods>;
}

#############################################
# FIREBASE (Auth, Firestore, Analytics)
#############################################
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keepattributes Signature
-keepattributes *Annotation*

#############################################
# REVENUECAT
#############################################
-keep class com.revenuecat.purchases.** { *; }
-dontwarn com.revenuecat.purchases.**

#############################################
# SQLDELIGHT
#############################################
# Keep generated models + adapters
-keep class com.squareup.sqldelight.** { *; }
-dontwarn com.squareup.sqldelight.**

#############################################
# SKIE (for Kotlin Multiplatform)
#############################################
-keep class co.touchlab.skie.** { *; }
-dontwarn co.touchlab.skie.**

#############################################
# MOSHI / GSON (if used for JSON)
#############################################
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

#############################################
# REFLECTION-BASED (SAFE DEFAULTS)
#############################################
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes *Annotation*

# Keep enum values (needed for many libs)
-keepclassmembers enum * { *; }

#############################################
# LOGGING (ignore warnings)
#############################################
-dontwarn java.lang.invoke.*
# Ignore missing JPEG2000 decoder (PdfBox optional dependency)
-dontwarn com.gemalto.jp2.**
# Keep all Ktor classes to prevent issues with the code shrinker (R8/ProGuard)
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
