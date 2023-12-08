plugins {
    id("convention.feature")
}

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