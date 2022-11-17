@file:Suppress("UNUSED_VARIABLE")

import ru.art2000.modules.setupKmmModule
import ru.art2000.modules.kapt

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

setupKmmModule()

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":extensions"))
            }
        }
        val androidMain by getting {
            dependencies {

                implementation(project(":extensions"))

                implementation(libs.lifecycle.viewmodel)

                implementation(libs.bundles.room.impl)

                implementation(libs.constraintlayout)

                implementation(libs.bundles.hilt.impl)
                kapt(libs.bundles.hilt.kapt)
            }
        }
    }
}

android {
    defaultConfig {
        buildConfigField("boolean", "USE_COMPOSE", "true")
    }
}
