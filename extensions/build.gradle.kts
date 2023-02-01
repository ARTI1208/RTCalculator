@file:Suppress("UNUSED_VARIABLE")

import ru.art2000.modules.setupKmmModule

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

setupKmmModule("ru.art2000")

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.datetime)
                implementation(libs.coroutines.core)
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.lifecycle.process)
            }
        }
    }
}