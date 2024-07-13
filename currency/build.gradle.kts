plugins {
    id("convention.feature")
}

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
                fun implementation(
                    dependencyProvider: Provider<MinimalExternalModuleDependency>,
                    dependencyConfiguration: ExternalModuleDependency.() -> Unit
                ) = "implementation"(dependencyProvider.get(), dependencyConfiguration)

                val okhttpGroup = libs.okhttp.get().group
                implementation(libs.retrofit.retrofit) {
                    exclude(group = okhttpGroup)
                }
                implementation(libs.retrofit.convertersimplexml) {
                    exclude(group = okhttpGroup)
                }

                implementation(libs.okhttp)
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
