plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val kotlinVersion: String by project
    val bintrayVersion: String by project
    api("org.jetbrains.kotlin", "kotlin-gradle-plugin", kotlinVersion)
    api("com.jfrog.bintray.gradle", "gradle-bintray-plugin", bintrayVersion)
}