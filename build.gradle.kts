import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.21"
    id("kr.lanthanide.mindugradle") version "1.1"
}

version = "1.0"

val mindustryVersion = "v158"
val sdkRoot = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
val isWindows = System.getProperty("os.name").lowercase().contains("windows")
val modArtifactName = project.name

sourceSets.main {
    kotlin.srcDirs("src")
    java.srcDirs("src")
}

repositories {
    mavenCentral()

    ivy {
        url = uri("https://github.com/")
        patternLayout {
            artifact("/[organisation]/[module]/releases/download/[revision]/dependencies.jar")
        }
        metadataSources { artifact() }
    }

    ivy {
        url = uri("https://github.com/")
        patternLayout {
            artifact("/[organisation]/[module]/releases/download/master/[revision].jar")
        }
        metadataSources { artifact() }
    }
}

dependencies {
    val useLatest = false

    compileOnly(if (useLatest) "Anuken:MindustryBuilds:latest" else "Anuken:Mindustry:$mindustryVersion")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.release = 8
}

mindugradle {
    jvmArgs.set(listOf("-Xmx2G", "-Xms512M"))
}

tasks.register("jarAndroid") {
    dependsOn("jar")

    doLast {
        if (sdkRoot == null || !File(sdkRoot).exists()) throw GradleException("No valid Android SDK found. Ensure that ANDROID_HOME is set to your Android SDK directory.")

        val platformRoot = File("$sdkRoot/platforms/").listFiles()?.sortedDescending()?.find { File(it, "android.jar").exists() }
            ?: throw GradleException("No android.jar found. Ensure that you have an Android platform installed.")

        val classpathArgs =
            (project.configurations.compileClasspath.get() + project.configurations.runtimeClasspath.get() + listOf(
                File(
                    platformRoot,
                    "android.jar"
                )
            )).joinToString(" ") { "--classpath ${it.path}" }

        val d8 = if (isWindows) "d8.bat" else "d8"

        project.exec {
            workingDir = file("${layout.buildDirectory}/libs")
            commandLine(d8, *classpathArgs.split(" ").toTypedArray(), "--min-api", "14", "--output", "${modArtifactName}Android.jar", "${modArtifactName}Desktop.jar")
        }
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("${modArtifactName}Desktop.jar")

    from({
        configurations.runtimeClasspath.map {
            it.asFileTree
        }
    })

    from(rootDir) {
        include("mod.hjson")
    }

    from("assets/") {
        include("**")
    }
}

tasks.register<Jar>("deploy") {
    dependsOn("jarAndroid", "jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("${modArtifactName}.jar")

    from(zipTree("${layout.buildDirectory}/libs/${modArtifactName}Desktop.jar"))
    from(zipTree("${layout.buildDirectory}/libs/${modArtifactName}Android.jar"))

    doLast {
        project.delete("${layout.buildDirectory}/libs/${modArtifactName}Desktop.jar")
        project.delete("${layout.buildDirectory}/libs/${modArtifactName}Android.jar")
    }
}
