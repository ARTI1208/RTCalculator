@file:Suppress("UNUSED_VARIABLE")

import ru.art2000.modules.setupFeatureModule
import ru.art2000.modules.kapt

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

setupFeatureModule()

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.datetime)
                implementation(libs.coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {

                implementation("org.apache.commons:commons-math3:3.6.1")
                implementation("com.github.hannesa2:AndroidSlidingUpPanel:4.5.0")

                implementation(libs.datetime)

                implementation(libs.bundles.room.impl)
                kapt(libs.bundles.room.kapt)
            }
        }
    }
}