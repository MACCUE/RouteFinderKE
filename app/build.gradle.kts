import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// 1. Load the local.properties file to keep M-Pesa keys safe
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.routefinderke"
    // Updated to 36 as required by newer androidx.activity dependencies
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.routefinderke"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 2. Inject M-Pesa keys from local.properties into Java BuildConfig
        // We strip any existing quotes from the property value and then wrap it in one set of quotes
        // to ensure the generated Java code has a valid String literal: "value"
        fun getSafeProperty(key: String): String {
            val value = localProperties.getProperty(key) ?: ""
            return "\"${value.replace("\"", "")}\""
        }

        buildConfigField("String", "MPESA_CONSUMER_KEY", getSafeProperty("MPESA_CONSUMER_KEY"))
        buildConfigField("String", "MPESA_CONSUMER_SECRET", getSafeProperty("MPESA_CONSUMER_SECRET"))
        buildConfigField("String", "MPESA_PASSKEY", getSafeProperty("MPESA_PASSKEY"))
    }

    buildFeatures {
        // Required to access the keys via BuildConfig.MPESA_...
        buildConfig = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
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

    packaging {
        jniLibs {
            // Critical for Android 15+ compatibility (16KB page alignment)
            useLegacyPackaging = true
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core AndroidX & Material
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Google Maps and Location
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Networking (Retrofit & OkHttp)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Image & GIF Support (Glide)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // AR & Sceneview
    implementation("com.google.ar:core:1.47.0")
    implementation("io.github.sceneview:arsceneview:0.10.0")

    // UI Enhancements
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.preference:preference:1.2.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.7.0")
}
