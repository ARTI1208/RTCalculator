plugins {
    id("convention.feature")
}

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.calculator)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(projects.calculator)
            }
        }
    }
}