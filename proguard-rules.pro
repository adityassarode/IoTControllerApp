# IoTControllerApp/proguard-rules.pro

# Keep classes required for Parcelable/Serializable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep all annotated classes for Gson, Moshi, or other JSON serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @com.squareup.moshi.Json <fields>;
}

# Keep Application, Activity, and Service subclasses
-keep class ** extends android.app.Activity
-keep class ** extends android.app.Service
-keep class ** extends android.app.Application

# Keep Compose generated classes (important for Jetpack Compose)
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class kotlinx.coroutines.** { *; }

# Common safe ignore for OkHttp, Retrofit, etc
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# (Optional) Keep utility classes, if you use reflection
-keep class com.yourdomain.iotcontroller.** { *; }

# Remove debugging information
-dontoptimize
-dontpreverify
