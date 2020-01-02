package com.githb.lamba92.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.setValue
import java.io.File

@CacheableTask
open class GenerateDefForFramework : DefaultTask() {

    @get:OutputFile
    var output by project.objects.property<File>()

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var framework by project.objects.property<File>()

    @get:Input
    var packageName by project.objects.property<String>()

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var libraryPaths by project.objects.listProperty<File>()

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var staticLibraries by project.objects.listProperty<File>()

    @TaskAction
    private fun generate(): Unit = with(project) {
        val headersNames = File(framework, "Headers")
            .walkTopDown()
            .filter { it.isFile }
            .map { it.name }
            .joinToString(" ")

        val defContent = buildString {
            appendln("language = Objective-C")
            appendln("package = $packageName")
            appendln("staticLibraries = ${staticLibraries.joinToString(" ") { it.name }}")
            appendln("libraryPaths = ${libraryPaths.joinToString(" ") { it.absolutePath }}")
            appendln("headers = $headersNames")
        }

        with(output) {
            if (exists())
                delete()
            parentFile.mkdirs()
            createNewFile()
            writeText(defContent)
        }

    }

}