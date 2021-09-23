/*
 * Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

object Libs {
    val kotlinStdLib by lazy { "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}" }
    val kotlinStdLibJdk7 by lazy { "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.KOTLIN}" }
    val coroutinesCore by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}" }
    val coroutinesAndroid by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}" }
    val okhttp by lazy { "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}" }
    val roomRuntime by lazy { "androidx.room:room-runtime:${Versions.ROOM}" }
    val roomKtx by lazy { "androidx.room:room-ktx:${Versions.ROOM}" }
    val roomCompiler by lazy { "androidx.room:room-compiler:${Versions.ROOM}" }
    val startup by lazy { "androidx.startup:startup-runtime:${Versions.STARTUP}" }
    val ktx by lazy { "androidx.core:core-ktx:${Versions.KTX}" }
    val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.APP_COMPAT}" }
    val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}" }
    val lifecycleKtx by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE_KTX}" }
    val kotlinPoet by lazy { "com.squareup:kotlinpoet:${Versions.KOTLIN_POET}" }
    val autoService by lazy { "com.google.auto.service:auto-service:${Versions.AUTO_SERVICE}" }
    val okio by lazy { "com.squareup.okio:okio:${Versions.OKIO}" }

    val junit by lazy { "junit:junit:${Versions.JUNIT}" }
    val mockk by lazy { "io.mockk:mockk:${Versions.MOCKK}" }
    val turbine by lazy { "app.cash.turbine:turbine:${Versions.TURBINE}" }
    val junitExt by lazy { "androidx.test.ext:junit:${Versions.JUNIT_EXT}" }
    val espressoCore by lazy { "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}" }
}
