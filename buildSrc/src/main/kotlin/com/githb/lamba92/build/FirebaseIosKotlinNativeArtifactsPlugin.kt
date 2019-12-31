package com.githb.lamba92.build

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.util.prefixIfNot

class FirebaseIosKotlinNativeArtifactsPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply<MavenPublishPlugin>()
        apply<KotlinMultiplatformPluginWrapper>()
        apply<BintrayPlugin>()

        val fName = project.name
            .capitalize()
            .prefixIfNot("Firebase")

        kotlin {

            ios()
            tvos()
            macosX64()
            watchos()

            targets.withType<KotlinNativeTarget> {
                compilations["main"].cinterops {
                    bindFirebaseFramework(fName, project)
                }
            }
        }

        publishing.publications.withType<MavenPublication> {
            artifactId = "kt-firebase-$artifactId"
        }

        val bintrayUsername = searchPropertyOrNull("bintrayUsername")
            ?: searchPropertyOrNull("BINTRAY_USERNAME")
        val bintrayApiKey = searchPropertyOrNull("bintrayApiKey")
            ?: searchPropertyOrNull("BINTRAY_API_KEY")

        if (bintrayApiKey != null && bintrayUsername != null)
            bintray {
                user = bintrayUsername
                key = bintrayApiKey
                pkg {
                    version {
                        name = project.version.toString()
                    }
                    repo = "com.github.lamba92"
                    name = "kt-firebase"
                    setLicenses("Apache-2.0")
                    vcsUrl = "https://github.com/lamba92/firebase-ios-kotlin-native"
                    issueTrackerUrl = "https://github.com/lamba92/firebase-ios-kotlin-native/issues"
                }
                publish = true

                publishing {
                    setPublications(publications.names)
                }
                println("\n> :${project.name}\n" +
                        " - Set up publications names: ${publications.joinToString()}")
            }
        else
            println("\n> :${project.name}\n" +
                    " - Publishing credentials not found.")

    }
}