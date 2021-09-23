plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.github.dcendents.android-maven")
}

group = "com.github.ivanisidrowu"

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.time.ExperimentalTime" + "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

    lintOptions {
        isAbortOnError = false
    }
}

dependencies {
    api(project(":kotlin"))
    implementation(Libs.kotlinStdLib)
    implementation(Libs.ktx)
    implementation(Libs.appCompat)
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.junitExt)
    androidTestImplementation(Libs.espressoCore)

    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    implementation(Libs.okhttp)

    implementation(Libs.roomRuntime)
    implementation(Libs.roomKtx)
    kapt(Libs.roomCompiler)

    implementation(Libs.startup)

    testImplementation(Libs.mockk)
    testImplementation(Libs.turbine)

    androidTestImplementation(Libs.mockk)

    testImplementation(project(":testCommon"))
    androidTestImplementation(project(":testCommon"))
}