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
        ":core", ":auth", ":storage", ":firestore", ":database",
        ":dynamicLinks", ":functions", ":messaging"
    )

include(*toBuild)

rootProject.name = "firebase-ios-kotlin-native-artifacts"
