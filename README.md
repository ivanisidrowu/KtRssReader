# KtRssReader


[![](https://jitpack.io/v/ivanisidrowu/KtRssReader.svg)](https://jitpack.io/#ivanisidrowu/KtRssReader)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Android Weekly](https://img.shields.io/badge/Android%20Weekly-%23435-red.svg)](http://androidweekly.net/issues/issue-435)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-KtRssReader-yellow.svg?style=flat)](https://android-arsenal.com/details/1/8178)

KtRssReader is a Kotlin library for parsing RSS feed.

## Features

### Android
* Supports RSS 2.0 standard, iTunes, and Google Play tags
* Easy-to-use API
* Fetches feed for you
* Database cache and custom cache valid time
* Customizes output data with annotations

### Kotlin
* Supports RSS 2.0 standard, iTunes, and Google Play tags
* Easy-to-use API

## Download

### Android
First, you can add this repository to the root of your project `build.gradle` file under the **`allprojects`**.

```gradle
allprojects {
  repositories {
   ...
   maven { url 'https://jitpack.io' }
  }
}
```

Then, add this dependency to the `build.gradle` file in app directory.

```gradle
dependencies {
    implementation "com.github.ivanisidrowu.KtRssReader:android:v2.0.2"
}
```

If you want to customize data format, you have to add these dependencies.

```gradle
apply plugin: 'kotlin-kapt'

dependencies {
    implementation "com.github.ivanisidrowu.KtRssReader:android:v2.0.2"
    implementation "com.github.ivanisidrowu.KtRssReader:annotation:v2.0.2"
    kapt "com.github.ivanisidrowu.KtRssReader:processor:v2.0.2"
}
```

### Kotlin
First, you can add this repository to the root of your project `build.gradle` file under the **`allprojects`**.

```gradle
allprojects {
  repositories {
   ...
   maven { url 'https://jitpack.io' }
  }
}
```

Then, add this dependency to the `build.gradle` file in app directory.

```gradle
dependencies {
    implementation "com.github.ivanisidrowu.KtRssReader:kotlin:v2.0.2"
}
```

## How to Use KtRssReader?
Please check out documentation for more information.

**[Android](https://github.com/ivanisidrowu/KtRssReader/wiki/Android)**

**[Kotlin](https://github.com/ivanisidrowu/KtRssReader/wiki/Kotlin)**

## Contribution

Contributions are always welcome. If you have any ideas or suggestions, you can contact us or create a Github issue. We will get to you as soon as possible.

## License

```
Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
