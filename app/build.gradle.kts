plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

// Datos del repositorio publico usados por el actualizador dentro de la app.
val githubOwner = "Johanfer12"
val githubRepo = "LinguaPulse"

// La build de CI usa el numero de ejecucion para tener un versionCode siempre creciente,
// de modo que la app pueda detectar automaticamente cada cambio publicado.
val ciRunNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()
val appVersionCode = if (ciRunNumber != null) 1000 + ciRunNumber else 1
val appVersionName = "1.1.0-beta" + (ciRunNumber?.let { ".$it" } ?: "")

android {
    namespace = "com.antigravity.linguapulse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.antigravity.linguapulse"
        minSdk = 26
        targetSdk = 34
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GITHUB_OWNER", "\"$githubOwner\"")
        buildConfigField("String", "GITHUB_REPO", "\"$githubRepo\"")
    }

    // Keystore estable compartido por todas las builds publicadas.
    // GitHub Actions lo genera y lo versiona la primera vez; a partir de ahi
    // todas las releases quedan firmadas igual y la app puede actualizarse sola.
    val betaKeystore = rootProject.file("signing/linguapulse-beta.jks")

    signingConfigs {
        create("beta") {
            if (betaKeystore.exists()) {
                storeFile = betaKeystore
                storePassword = "linguapulse"
                keyAlias = "linguapulse"
                keyPassword = "linguapulse"
            }
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = if (betaKeystore.exists()) signingConfigs.getByName("beta") else signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            // Reporta funciones composables inestables durante la compilacion.
            "-opt-in=kotlin.RequiresOptIn"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/*.version"
            excludes += "DebugProbesKt.bin"
            excludes += "kotlin-tooling-metadata.json"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.04.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Instala el Baseline Profile de Compose: evita el JIT en el primer arranque
    // y es la mejora de fluidez mas grande en dispositivos de gama media.
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // WorkManager for background scheduled notifications
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Debugging / Preview
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
