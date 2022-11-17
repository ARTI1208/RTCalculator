import ru.art2000.modules.setupFeatureModule
import java.util.*

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

setupFeatureModule()

android {
    defaultConfig {
        buildConfigField("long", "BUILD_TIME", "${Date().time}")
    }
}