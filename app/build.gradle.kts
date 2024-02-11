plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.workin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.workin"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packagingOptions{
        resources.excludes.add("META-INF/*")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //junit test 5.8
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    //digger hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    //firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.1") // Fire store
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.1") //Crashlytics
    implementation("com.google.firebase:firebase-perf-ktx:20.5.1") // performance monitoring
    implementation("com.google.firebase:firebase-database-ktx:20.3.0") //firebase Database
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0") //firebase Storage

    //splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")
    //navigation
    implementation("androidx.navigation:navigation-common-ktx:2.7.6")
    //android lifecycle extensions
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    //navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.7.6")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.6")
    //sdp
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

kapt{
    correctErrorTypes = true
}