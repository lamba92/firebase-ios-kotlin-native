package com.githb.lamba92.build

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.prefixIfNot
import java.io.File

class FirebaseIosKotlinNativeArtifactsPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {

        project.projectDir.mkdirs()

        apply<MavenPublishPlugin>()
        apply<KotlinMultiplatformPluginWrapper>()
        apply<BintrayPlugin>()

        val generatedSourcesPath = "$buildDir/generated/dummy"

        val fName = project.name
            .capitalize()
            .prefixIfNot("Firebase")

        val extractFirebase by extractFirebaseIosZipProvider

        val generateDefFileTask =
            task<GenerateDefForFramework>("generate${fName}DefFile") {
                dependsOn(extractFirebase)
                val rootFrameworkDir = File(extractFirebase.destinationDir, "$fName.framework")
                framework = rootFrameworkDir
                libraryPaths = listOf(rootFrameworkDir)
                staticLibraries = listOf(File(rootFrameworkDir, fName))
                packageName = "com.google.firebase"
                output = file("$buildDir/interop/def/$fName.def")
            }

        kotlin {

            ios()
            tvos()
            macosX64()
            watchos()

            targets.withType<KotlinNativeTarget> {
                compilations["main"].cinterops {
                    create(fName) {
                        defFile = generateDefFileTask.output
                        includeDirs(file("${generateDefFileTask.framework.absolutePath}/Headers"))
                        compilerOpts("-F${generateDefFileTask.framework.parentFile.absolutePath}")
                    }
                }
            }

            sourceSets {
                val generatedMain by creating {
                    kotlin.setSrcDirs(listOf("$generatedSourcesPath/kotlin"))
                    resources.setSrcDirs(listOf("$generatedSourcesPath/resources"))
                }
                @Suppress("UNUSED_VARIABLE")
                val commonMain by getting {
                    dependsOn(generatedMain)
                }
            }
        }

        tasks.withType<CInteropProcess> {
            dependsOn(generateDefFileTask)
        }

        val generateDummySourceTask = task("generateDummySource") {
            doLast {
                File("$generatedSourcesPath/kotlin", "Dummy.kt")
                    .apply {
                        if (exists())
                            delete()
                        parentFile.mkdirs()
                        createNewFile()
                    }
                    .writeText("private val dummy = \"dummy\"")
            }
        }

        tasks.withType<KotlinCompile> {
            dependsOn(generateDummySourceTask)
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
                println(
                    "\n> :${project.name}\n" +
                            " - Set up publications names: ${publications.joinToString()}"
                )
            }
        else
            println(
                "\n> :${project.name}\n" +
                        " - Publishing credentials not found."
            )

    }
}