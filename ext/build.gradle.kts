plugins {
    id("convention.kmp")
}

android {
    namespace = "ru.art2000.extensions"
}

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.datetime)
                implementation(libs.coroutines.core)
                implementation(libs.multiplatformSettings)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.lifecycle.process)
            }
        }
    }
}