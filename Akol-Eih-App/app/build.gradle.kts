plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.akoleih"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.akoleih"
        minSdk = 30
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    // ...

    // Import the Firebase BoM
//    implementation(platform(libs.firebase.bom))

    // When using the BoM, you don't specify versions in Firebase library dependencies

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation(libs.firebase.analytics)

    // TODO: Add the dependencies for any other Firebase products you want to use
    // See https://firebase.google.com/docs/android/setup#available-libraries
    // For example, add the dependencies for Firebase Authentication and Cloud Firestore
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    // Import the BoM for the Firebase platform
//    implementation(platform(libs.firebase.bom))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
//    implementation(libs.google.firebase.auth)
    implementation (libs.material.v1110)



    implementation (libs.play.services.auth)



    // ✅ Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // ✅ Glide
    implementation(libs.glide)
    implementation (libs.cardview)
    implementation (libs.lottie)


    // Responsive sizing
    implementation (libs.sdp.android)
    implementation (libs.ssp.android)

    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
}