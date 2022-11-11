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

                implementation(libs.bundles.room.impl)
                kapt(libs.bundles.room.kapt)

                implementation("androidx.work:work-runtime-ktx:2.7.1")

                implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

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


                val okhttpVersionMinApi16 = "3.12.13"
                val retrofitVersionMinApi16 = "2.6.4"

                //noinspection GradleDependency
                "minApi16Implementation"("com.squareup.okhttp3:okhttp:$okhttpVersionMinApi16")
                //noinspection GradleDependency
                "minApi16Implementation"("com.squareup.okhttp3:okhttp-urlconnection:$okhttpVersionMinApi16")
                //noinspection GradleDependency
                "minApi16Implementation"("com.squareup.retrofit2:retrofit:$retrofitVersionMinApi16")
                //noinspection GradleDependency
                "minApi16Implementation"("com.squareup.retrofit2:converter-simplexml:$retrofitVersionMinApi16") {
                    exclude(group = "stax", module = "stax-api")
                    exclude(group = "stax", module = "stax")
                    exclude(group = "xpp3", module = "xpp3")
                }


                val okhttpVersionMinApi21 = "4.10.0"
                val retrofitVersionMinApi21 = "2.9.0"

                "minApi21Implementation"("com.squareup.okhttp3:okhttp:$okhttpVersionMinApi21")
                "minApi21Implementation"("com.squareup.okhttp3:okhttp-urlconnection:$okhttpVersionMinApi21")

                "minApi21Implementation"("com.squareup.retrofit2:retrofit:$retrofitVersionMinApi21")
                "minApi21Implementation"("com.squareup.retrofit2:converter-simplexml:$retrofitVersionMinApi21") {
                    exclude(group = "stax", module = "stax-api")
                    exclude(group = "stax", module = "stax")
                    exclude(group = "xpp3", module = "xpp3")
                }
            }
        }
    }
}