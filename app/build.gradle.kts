plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services") // Firebase
}

android {
    namespace = "com.example.triathlon360"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.triathlon360"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // -------------------------
    // Android Core
    // -------------------------
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // -------------------------
    // Lifecycle
    // -------------------------
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // -------------------------
    // Room DB
    // -------------------------
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // -------------------------
    // 🔥 MPAndroidChart (FIXES ALL BarChart ERRORS)
    // -------------------------
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // -------------------------
    // Firebase
    // -------------------------
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
}
