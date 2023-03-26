@file:Suppress("UNUSED_VARIABLE")

import ru.art2000.modules.setupKmmModule

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

setupKmmModule()

// TODO remove when native.cocoapods properly supports gradle 8
val myAttribute = Attribute.of("configurationDisambiguation", String::class.java)

val archTypes = listOf("Arm64", "SimulatorArm64", "X64", "Fat")
val buildTypes = listOf("debug", "release")
val podTypes = listOf("", "pod")

val names = podTypes.flatMap { pod ->
    buildTypes.flatMap { build ->
        val podBuild = when {
            pod.isEmpty() -> build
            else -> "$pod${build.replaceFirstChar { it.uppercaseChar() }}"
        }
        archTypes.map { arch ->
            "${podBuild}FrameworkIos$arch"
        }
    }
}

configurations.configureEach {
    if (name in names) {
        attributes {
            attribute(myAttribute, name)
        }
    }
}
// END

kotlin {

    val projects = listOf(
        project(":extensions"),
        project(":common"),
        project(":calculator"),
        project(":unit"),
        project(":currency"),
        project(":settings"),
    )

    cocoapods {
        summary = "Calculator Umbrella Shared Module"
        homepage = "https://github.com/ARTI1208/Calculator/shared"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = project.name
            projects.forEach { export(it) }
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                projects.forEach { api(it) }
            }
        }
        val androidMain by getting {
            dependencies {
                projects.forEach { implementation(it) }
            }
        }
        val iosMain by getting {
            dependencies {
                projects.forEach { api(it) }
            }
        }
    }
}