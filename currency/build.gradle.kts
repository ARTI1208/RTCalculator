@file:Suppress("UNUSED_VARIABLE")

import ru.art2000.modules.setupFeatureModule
import ru.art2000.modules.kapt

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

setupFeatureModule()

configurations.all {
    exclude(group = "stax", module = "stax-api")
    exclude(group = "stax", module = "stax")
    exclude(group = "xpp3", module = "xpp3")
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

                implementation(libs.bundles.room.impl)
                kapt(libs.bundles.room.kapt)

                implementation(libs.work)

                implementation(libs.swiperefreshlayout)

                // TODO rework when kotlin multiple receivers will be available
                operator fun String.invoke(dependencyNotation: Any) {
                    project.dependencies.add(this, dependencyNotation)
                }

                operator fun String.invoke(
                    dependencyNotation: Any,
                    dependencyConfiguration: ExternalModuleDependency.() -> Unit
                ) {
                    project.dependencies.add(this, dependencyNotation).apply {
                        (this as ExternalModuleDependency).dependencyConfiguration()
                    }
                }

                val okhttpGroup = libs.okhttp.okhttpMinApi16.get().group
                "minApi16Implementation"(libs.okhttp.okhttpMinApi16)
                "minApi16Implementation"(libs.retrofit.retrofitMinApi16.get()) {
                    exclude(group = okhttpGroup)
                }
                "minApi16Implementation"(libs.retrofit.convertersimplexmlMinApi16.get()) {
                    exclude(group = okhttpGroup)
                }

                "minApi21Implementation"(libs.bundles.network.minApi21)
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