package com.githb.lamba92.build

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.util.suffixIfNot
import java.io.File

fun NamedDomainObjectContainer<DefaultCInteropSettings>.bindFramework(
    framework: File,
    packageName: String,
    project: Project
): DefaultCInteropSettings = with(project) {

    val headersNames = File(framework, "Headers")
        .walkTopDown()
        .filter { it.isFile }
        .map { it.name }
        .joinToString(" ")

    maybeCreate(framework.name.removeSuffix(".framework")).apply {
        this.defFile = buildString {
            appendln("language = Objective-C")
            appendln("package = $packageName")
            appendln("staticLibraries = ${framework.name.removeSuffix(".framework")}")
            appendln("libraryPaths = ${framework.absolutePath}")
            appendln("headers = $headersNames")
        }
            .let {
                file("$buildDir/interop/def/${framework.nameWithoutExtension.removeSuffix(".framework")}.def")
                    .apply {
                        if (exists())
                            delete()
                        parentFile.mkdirs()
                        createNewFile()
                        writeText(it)
                    }
            }
        includeDirs(file("${framework.absolutePath}/Headers"))
        compilerOpts("-F${framework.parentFile.absolutePath}")
    }
}

fun NamedDomainObjectContainer<DefaultCInteropSettings>.bindFirebaseFramework(
    frameworkName: String,
    project: Project
): DefaultCInteropSettings = with(project) {
    val firebaseExtract by rootProject.tasks.named<Sync>("extractFirebaseIosZip")
    val firebasePackageName: String by project
    bindFramework(
        File(
            firebaseExtract.destinationDir,
            frameworkName.suffixIfNot(".framework")
        ),
        firebasePackageName,
        project
    )
}

fun BintrayExtension.pkg(action: BintrayExtension.PackageConfig.() -> Unit) {
    pkg(closureOf(action))
}

fun BintrayExtension.PackageConfig.version(action: BintrayExtension.VersionConfig.() -> Unit) {
    version(closureOf(action))
}

fun BintrayExtension.setPublications(names: Iterable<String>) =
    setPublications(*names.toList().toTypedArray())

fun BintrayExtension.setPublications(builder: () -> Iterable<String>) =
    setPublications(builder())

@Suppress("FunctionName")
fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) =
    extensions.configure(action)

@Suppress("FunctionName")
fun Project.bintray(action: BintrayExtension.() -> Unit) =
    extensions.configure(action)

@Suppress("FunctionName")
fun Project.publishing(action: PublishingExtension.() -> Unit) =
    extensions.configure(action)

val Project.publishing
    get() = extensions.getByType<PublishingExtension>()

fun Project.searchPropertyOrNull(propertyName: String): String? =
    findProperty(propertyName) as String? ?: System.getenv(propertyName)

fun Project.searchProperty(propertyName: String) =
    searchPropertyOrNull(propertyName)
        ?: throw IllegalArgumentException(
            "Cannot find $propertyName in either " +
                    "gradle/properties or environmental variables"
        )

fun Project.searchProperties(vararg propertyNames: String) =
    propertyNames.associate { it to searchPropertyOrNull(it) }