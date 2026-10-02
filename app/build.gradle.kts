plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.perbana"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.perbana"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("Boolean", "ENABLE_UPDATE_CHECK", "false")
            buildConfigField("String", "GITHUB_RELEASE_URL", "\"https://api.github.com/repos/RandyNasuta/Perbana/releases/latest\"")
            buildConfigField("String", "ENVIRONMENT", "\"Development\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
            buildConfigField("Boolean", "ENABLE_UPDATE_CHECK", "true")
            buildConfigField("String", "GITHUB_RELEASE_URL", "\"https://api.github.com/repos/RandyNasuta/Perbana/releases/latest\"")
            buildConfigField("String", "ENVIRONMENT", "\"Production\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.simplexml)

    //Gson
    implementation(libs.gson)

    //Okhttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //Glide
    implementation(libs.glide)
    implementation(libs.glide.vector)

    //Lottie
    implementation(libs.lottie)

    //Swipe Refresh Layout
    implementation(libs.swipe.refresh.layout)

    //Work Manager
    implementation(libs.work.runtime)

    //Play service location
    implementation(libs.play.service.location)
}