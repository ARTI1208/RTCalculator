@file:Suppress("UNUSED_VARIABLE")

import ru.art2000.modules.setupFeatureModule

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

setupFeatureModule()

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":calculator"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(project(":calculator"))
            }
        }
    }
}