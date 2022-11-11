@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.dagger) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.kotlin.gradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    tasks.withType<JavaCompile> {
        options.compilerArgs.plusAssign(listOf(
            "-Xlint:unchecked", "-Xlint:deprecation"
        ))
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
