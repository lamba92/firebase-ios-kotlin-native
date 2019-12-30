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

include(":core", ":auth", ":storage", ":firestore")
rootProject.name = "firebase-ios-kotlin-native-artifacts"

