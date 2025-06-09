plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    kotlin("kapt")
}

android {
    namespace = "com.fake.piggybudgetapp"
    compileSdk = 35
    viewBinding.enable = true

    defaultConfig {
        applicationId = "com.fake.piggybudgetapp"
        minSdk = 31
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage.ktx)
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    kapt ("com.github.bumptech.glide:compiler:4.16.0")
//    implementation(libs.androidx.ui.text.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation ("com.google.android.material:material:1.11.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.google.firebase:firebase-storage-ktx")
    implementation ("net.objecthunter:exp4j:0.4.8")
}