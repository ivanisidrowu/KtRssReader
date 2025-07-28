// Without these suppressions version catalog usage here and in other build
// files is marked red by IntelliJ:
// https://youtrack.jetbrains.com/issue/KTIJ-19369.
@Suppress(
    "DSL_SCOPE_VIOLATION",
)
plugins {
    alias(libs.plugins.ksp)
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "tw.ktrssreader.sample"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "tw.ktrssreader.sample"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
}

dependencies {
    implementation(project(":android"))
    implementation(project(":annotation"))
    ksp(project(":processor"))
    implementation(libs.kotlinStdlib)
    implementation(libs.coreKtx)
    implementation(libs.appCompat)
    implementation(libs.bundles.coroutines)
    implementation(libs.constraintLayout)
    implementation(libs.lifecycleRuntimeKtx)

    // testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.junitAndroid)
    androidTestImplementation(libs.espressoCore)
}
