plugins {
    id("convention.kmp")
    id("convention.dagger")
}

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.ext)
            }
        }
        val androidMain by getting {
            dependencies {

                implementation(projects.ext)

                implementation(libs.lifecycle.viewmodel)

                implementation(libs.bundles.room.impl)

                implementation(libs.constraintlayout)
            }
        }
    }
}

android {
    defaultConfig {
        buildConfigField("boolean", "USE_COMPOSE", "true")
    }
}
