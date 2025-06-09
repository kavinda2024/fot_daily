plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fotdaily.fot_daily"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fotdaily.fot_daily"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Use the versions defined in libs.versions.toml
    implementation(libs.appcompat) // androidx.appcompat:appcompat
    implementation(libs.material) // com.google.android.material:material
    implementation(libs.activity) // androidx.activity:activity
    implementation(libs.constraintlayout) // androidx.constraintlayout:constraintlayout

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
// Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    // Add CardView explicitly for CardView component
    implementation("androidx.cardview:cardview:1.0.0") // For CardView

    // Testing dependencies
    testImplementation(libs.junit) // junit:junit
    androidTestImplementation(libs.ext.junit) // androidx.test.ext:junit
    androidTestImplementation(libs.espresso.core) // androidx.test.espresso:espresso-core
}
