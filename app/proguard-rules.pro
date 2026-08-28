# Keep model classes
-keep class com.wuji.app.data.model.** { *; }
-keep class com.wuji.app.data.local.entity.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.wuji.app.**$$serializer { *; }
-keepclassmembers class com.wuji.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.wuji.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Ktor
-dontwarn io.ktor.**
-dontwarn org.slf4j.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Media3
-keep class androidx.media3.** { *; }

# Room
-keep class androidx.room.** { *; }
