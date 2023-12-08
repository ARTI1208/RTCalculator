import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val libs = the<LibrariesForLibs>()

dependencies {
    val unifiedImplementation = when (val extension = kotlin as? KotlinMultiplatformExtension) {
        null -> "implementation"
        else -> extension.sourceSets.getByName("androidMain").implementationConfigurationName
    }

    val unifiedKsp = when (kotlin as? KotlinMultiplatformExtension) {
        null -> "ksp"
        else -> "kspAndroid"
    }

    unifiedImplementation(libs.bundles.hilt.impl)

    unifiedKsp(libs.bundles.hilt.preprocessing)
}

val Project.kotlin: KotlinProjectExtension
    get() = extensions.getByName("kotlin") as KotlinProjectExtension