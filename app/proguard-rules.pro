# LinguaPulse — reglas de R8/ProGuard para la build de release.

# Mantener los nombres de linea para que los crash reports sean legibles.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room genera implementaciones por reflexion en tiempo de compilacion (KSP),
# pero necesita conservar las entidades y el DAO generado.
-keep class com.antigravity.linguapulse.data.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# WorkManager instancia los Workers por nombre de clase.
-keep class com.antigravity.linguapulse.notifications.CardNotificationWorker { *; }
-keep class * extends androidx.work.ListenableWorker { public <init>(...); }

# Modelos del actualizador (se construyen desde JSON).
-keep class com.antigravity.linguapulse.update.** { *; }

# org.json viene del framework de Android.
-dontwarn org.json.**
