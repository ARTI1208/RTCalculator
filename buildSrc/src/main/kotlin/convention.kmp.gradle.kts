import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("com.android.library")
    id("convention.android.base")
}

val libs = the<LibrariesForLibs>()

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = project.name
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(libs.bundles.kotest.common)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.bundles.kotest.jvm)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.koin.core)
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(listOf(
            "-Xexpect-actual-classes",
        ))
    }
}

android {
    sourceSets {
        val main by getting
        main.manifest {
            srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}
