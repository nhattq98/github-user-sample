# Keep model classes (e.g., for Retrofit, Gson, Moshi)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.tahn.assignment.model.** { *; }
-keep class com.tahn.assignment.local.database.entity.** { *; }

# Keep Retrofit interfaces
-keep interface retrofit2.Call
-keep class retrofit2.** { *; }

# Koin core
-keep class org.koin.** { *; }

# Keep your DI modules and classes
-keep class com.tahn.assignment.di.** { *; }
-dontwarn org.koin.**

# Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep classes used in reflection (used by many libraries)
-keepclassmembers class * {
    *** get*(...);
    void set*(...);
}

# Keep Kotlin Metadata (important for data classes, sealed classes)
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature, Exceptions, LineNumberTable, LocalVariableTable, SourceFile, *AnnotationDefault*

# Prevent obfuscation of your app's main entry points
-keep class com.tahn.assignment.AssignmentApplication { *; }
-keep class com.tahn.assignment.MainActivity { *; }

# Keep Room database classes
-keep class androidx.room.** { *; }
-keep @androidx.room.* class * { *; }

# Keep classes annotated with your custom annotations (if any)
-keep @com.tahn.assignment.MyAnnotation class * { *; }

# Keep LiveData/ViewModel classes
-keep class androidx.lifecycle.** { *; }
-keep class com.example.viewmodel.** { *; }

# Fix common warnings
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn com.squareup.picasso.**
-dontwarn kotlin.coroutines.jvm.internal.**


# General Kotlin & Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.flow.** { *; }

# AndroidX DataStore
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# Paging 3
-keep class androidx.paging.** { *; }
-dontwarn androidx.paging.**
-keepclassmembers class * extends androidx.paging.PagingSource {
    public <init>(...);
}
-keepclassmembers class * extends androidx.paging.RemoteMediator {
    public <init>(...);
}

-keepclassmembers class ** {
    ** lambda*(...);
}

# Safe Args Plugin
-keep class **Args { *; }
-keep class **Directions { *; }