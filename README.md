# Firebase wrapper for Kotlin/Native Apple targets [![Build Status](https://travis-ci.org/lamba92/firebase-ios-kotlin-native.svg?branch=master)](https://travis-ci.org/lamba92/firebase-ios-kotlin-native)
You want to use firebase when targeting iOS with K/N? You are in the right place! This repo builds and publishes `.klib`s of cinterops for Firebase frameworks! 

You want them for K/MPP as well? Check out my [firebase-multiplatform](https://github.com/lamba92/firebase-multiplatform) project!

# Install [ ![Download](https://api.bintray.com/packages/lamba92/com.github.lamba92/kt-firebase/images/download.svg) ](https://bintray.com/lamba92/com.github.lamba92/kt-firebase/_latestVersion)
The libraries are built and published on Bintray. The version tag is composed of 2 parts: 
 - `{firebase versions}-{publication revision}`.
 
 For example, the bindings built for Firebase v6.14.0 may have versions `6.14.0-{something}`. For the first revision of a Firebase version the `-{something}` will be omitted!.
 At the moment the available modules are:
 - core
 - auth
 - storage
 - firestore
 
 **Your favorite module is missing?** Open an issue, I'll add it in the next build!
 
 While the available platforms are:
  - iosarm64
  - iosx64
  - maosx64
  - tvosarm64
  - tvosx64
  - watchosarm32
  - watchosarm64
  - watchosx86
  
```kotlin
repositories {
    maven("https://dl.bintray.com/lamba92/com.github.lamba92/")
}
// stuff
dependencies {
    implementation("com.github.lamba92:kt-firebase-$module-$platform:$version")
}
```