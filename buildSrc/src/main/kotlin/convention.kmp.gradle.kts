import com.android.build.api.dsl.ComposeOptions
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.TaskManager
import com.android.build.gradle.internal.component.ComponentCreationConfig
import com.android.build.gradle.internal.utils.addComposeArgsToKotlinCompile
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

val javaVersion = JavaVersion.toVersion(libs.versions.java.get())

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

    jvmToolchain(javaVersion.majorVersion.toInt())

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
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xexpect-actual-classes",
//            "-P",
//            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=1.9.20-RC2"
        )
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

android {
    sourceSets {
        val main by getting
        main.manifest {
            srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}
