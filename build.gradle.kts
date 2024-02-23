plugins {
    kotlin("jvm") version "1.9.22"
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(8)
}

abstract class MyJavaExec : JavaExec() {
    override fun exec() = println("allJvmArgs = \n${allJvmArgs.joinToString("\n")}")
}

val fileWriter by tasks.registering(WriteProperties::class) {
    destinationFile = layout.buildDirectory.file("foo.properties")
}

val foo by tasks.registering(MyJavaExec::class) {
    mainClass = "MainClass"
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgumentProviders.add(object : CommandLineArgumentProvider {
        @InputFile
        @PathSensitive(PathSensitivity.RELATIVE)
        val fileContent = fileWriter.map { it.outputs.files.singleFile }

        override fun asArguments() = listOf("-Dfoo=${fileContent.get().readText()}")
    })
}
