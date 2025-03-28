import java.util.*

plugins {
    id("convention.feature")
}

android {
    defaultConfig {
        buildConfigField("long", "BUILD_TIME", "${Date().time}")
    }

    buildFeatures {
        buildConfig = true
    }
}