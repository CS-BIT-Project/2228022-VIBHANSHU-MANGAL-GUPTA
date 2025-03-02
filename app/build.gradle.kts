
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.google.gms.google.services)
}


android {
    namespace = "com.example.plan_your_day"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.plan_your_day"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

dependencies {
    implementation (libs.androidx.constraintlayout.v214)
    implementation (libs.material.v150)
    implementation (libs.circleimageview)
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation (libs.androidx.appcompat.v141)
    implementation ("com.google.android.material:material:1.5.0")
    implementation (libs.androidx.recyclerview)
    implementation (libs.androidx.core.ktx.v170)
    implementation (libs.play.services.maps)   // Google Map SDK
    implementation (libs.play.services.location)    // Location Services API
    implementation (libs.google.android.maps.utils) // For polyline (route drawing)
    implementation (libs.okhttp) // For API calls (Directions API)
}


