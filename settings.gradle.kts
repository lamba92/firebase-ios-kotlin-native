@file:Suppress("UnstableApiUsage")

plugins {
    `gradle-enterprise`
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!System.getenv("CI").isNullOrEmpty())
    }
}

val toBuild = System.getenv("TO_BUILD")
    ?.replace(" ", "")
    ?.split(",")
    ?.map { if (it.startsWith(":")) it else ":$it" }
    ?.toTypedArray()
    ?: arrayOf(
        ":core", ":core-static" ,
        ":auth", ":auth-static",
        ":storage", ":storage-static",
        ":firestore", ":firestore-static",
        ":database", ":database-static",
        ":dynamicLinks", ":dynamicLinks-static",
        ":functions", ":functions-static",
        ":messaging", ":messaging-static"
    )

include(*toBuild)

rootProject.name = "firebase-ios-kotlin-native-artifacts"
enableFeaturePreview("GRADLE_METADATA")