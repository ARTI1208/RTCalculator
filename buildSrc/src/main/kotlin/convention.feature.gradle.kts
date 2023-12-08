import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    id("convention.kmp")
    id("convention.dagger")
}

val libs = the<LibrariesForLibs>()

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(project(":ext"))
            }
        }

        val androidMain by getting {
            dependencies {

                implementation(libs.lifecycle.viewmodel)
                implementation(libs.viewbindingdelegate)

                implementation(libs.bundles.room.impl)
                kspAndroid(libs.bundles.room.preprocessing)
            }
        }
    }
}

fun KotlinDependencyHandler.kspAndroid(dependencyNotation: Any) =
    project.dependencies.add("kspAndroid", dependencyNotation)
