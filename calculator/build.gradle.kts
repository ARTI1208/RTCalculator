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

                implementation(libs.apache.commons.math3)
                implementation(libs.slidingUpPanel)

                implementation(libs.datetime)

                implementation(libs.bundles.room.impl)
                kapt(libs.bundles.room.kapt)
            }
        }
    }
}

android {
    buildTypes {
        release {
            consumerProguardFile("proguard-rules.pro")
        }
    }
}