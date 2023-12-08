plugins {
    id("convention.kmp")
    kotlin("native.cocoapods")
}

kotlin {

    val projects = listOf(
        projects.ext,
        projects.common,
        projects.calculator,
        projects.unit,
        projects.currency,
        projects.settings,
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