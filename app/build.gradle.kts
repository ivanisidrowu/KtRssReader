
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "tw.ktrssreader"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = AndroidVersions.versionCode
        versionName = AndroidVersions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(Libs.kotlinStdLib)
    implementation(Libs.ktx)
    implementation(Libs.appCompat)
    implementation(Libs.constraintLayout)
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.junitExt)
    androidTestImplementation(Libs.espressoCore)

    implementation(project(":android"))
    implementation(project(":annotation"))
    kapt(project(":processor"))

    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)
    implementation(Libs.lifecycleKtx)
}