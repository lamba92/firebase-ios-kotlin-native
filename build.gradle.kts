import com.githb.lamba92.build.FirebaseIosKotlinNativeArtifactsPlugin
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions
import java.util.zip.ZipFile

plugins {
    kotlin("multiplatform") apply false
}

val firebaseVersion: String by project

allprojects {
    group = "com.github.lamba92"
    version = System.getenv("TRAVIS_TAG") ?: "0.0.1"
}

val firebaseIosSetupFolderName = "$buildDir/firebaseIosSetup"

val downloadFirebaseIos = task<de.undercouch.gradle.tasks.download.Download>("downloadFirebaseIosZip") {

    group = "ios firebase setup"

    val firebaseDownloadUrl: String by project

    val fileName = firebaseDownloadUrl.substringAfterLast("/")
    val file = file("$firebaseIosSetupFolderName/$fileName")

    onlyIf {
        if (file.exists())
            try {
                ZipFile(file)
                println("${file.absolutePath}:\n - Skipping $fileName download. Zip header integrity is fine.")
                false
            } catch (e: java.io.IOException) {
                true
            }
        else
            true
    }

    src(firebaseDownloadUrl)
    dest(file)
    doLast {
        Files.setPosixFilePermissions(file.toPath(), PosixFilePermissions.fromString("rw-rw-rw-"))
    }
}

val firebaseExtract = task<Sync>("extractFirebaseIosZip") {
    dependsOn(downloadFirebaseIos)
    group = "ios firebase setup"
    from(zipTree(downloadFirebaseIos.dest).matching { include("**/*.framework/**") }) {
        val frameRegex = Regex(".*[\\/\\\\](.*\\.framework[\\/\\\\].*)")
        eachFile {
            val newPath = frameRegex.matchEntire(path)!!.groups[1]!!.value
            path = newPath
        }
    }
    into("$firebaseIosSetupFolderName/${downloadFirebaseIos.dest.nameWithoutExtension}")
}

val skipBuild = findProperty("skipBuild")
    ?.toString()
    ?.toBoolean()
    ?: false

if (!skipBuild)
    subprojects {
        repositories {
            mavenCentral()
        }
        apply<FirebaseIosKotlinNativeArtifactsPlugin>()
        tasks {
            named("publishToMavenLocal") {
                dependsOn("build")
            }
        }
    }

