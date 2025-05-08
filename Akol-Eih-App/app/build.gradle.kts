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
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.auth)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.glide)
    implementation(libs.cardview)
    implementation(libs.sdp.android)
    implementation(libs.viewpager2)
    implementation(libs.constraintlayout.v214)
    implementation(libs.picasso)
    implementation(libs.core)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.savedstate)
    implementation(libs.room.runtime)
    implementation(libs.games.activity)
    annotationProcessor(libs.room.compiler)

    // MaterialCalendarView v2 (AndroidX-only)
    implementation("com.github.prolificinteractive:material-calendarview:2.0.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")

    implementation ("com.airbnb.android:lottie:6.6.6")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.8.0")

    implementation ("com.google.firebase:firebase-appcheck-playintegrity:18.0.0")
    testImplementation ("junit:junit:4.13.2")

    androidTestImplementation ("androidx.test:core:1.5.0")
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")



}
