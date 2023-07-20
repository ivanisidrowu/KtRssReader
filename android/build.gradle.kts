plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("maven-publish")
}

android {
    compileSdk = Version.compileSdk
    buildToolsVersion = Version.buildTool

    defaultConfig {
        minSdk = Version.minSdk
        targetSdk = Version.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        freeCompilerArgs =
            freeCompilerArgs + "-Xopt-in=kotlin.time.ExperimentalTime" + "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    api(project(":kotlin"))

    implementation(libs.kotlinStdlib)
    implementation(libs.coreKtx)
    implementation(libs.appCompat)
    implementation(libs.bundles.coroutines)
    implementation(libs.okhttp)
    implementation(libs.bundles.room)
    kapt(libs.roomCompiler)
    implementation(libs.startup)

    // testing
    testImplementation(project(":testCommon"))
    androidTestImplementation(project(":testCommon"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.junitAndroid)
    androidTestImplementation(libs.espressoCore)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.ivanisidrowu"
            artifactId = "KtRssReader"
            version = "v2.1.2"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
