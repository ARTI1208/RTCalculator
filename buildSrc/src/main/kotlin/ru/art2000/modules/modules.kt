@file:Suppress("UNUSED_VARIABLE")

package ru.art2000.modules

import com.android.build.api.dsl.ComposeOptions
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.TaskManager
import com.android.build.gradle.internal.component.ComponentCreationConfig
import com.android.build.gradle.internal.dsl.DefaultConfig
import com.android.build.gradle.internal.dsl.InternalCommonExtension
import com.android.build.gradle.internal.utils.addComposeArgsToKotlinCompile
import org.gradle.api.*
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private fun Project.version(alias: String) =
    extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findVersion(alias)
        .get()

private fun Project.library(alias: String) =
    extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findLibrary(alias)
        .get()

private fun Project.bundle(alias: String) =
    extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findBundle(alias)
        .get()

private fun Project.addCompose(flavor: ProductFlavor, options: ComposeOptions.() -> Unit = {}) {

    val composeOptions = object : ComposeOptions {
        override var kotlinCompilerExtensionVersion: String? = null

        @Deprecated("")
        override var kotlinCompilerVersion: String?
            get() = null
            set(_) { logger.warn("ComposeOptions.kotlinCompilerVersion is deprecated. Compose now uses the kotlin compiler defined in your buildscript.") }

        override var useLiveLiterals: Boolean= true

    }

    composeOptions.options()

    // adapted from [com.android.build.gradle.internal.TaskManager]

    val kotlinCompilerExtensionVersionInDsl =
        composeOptions.kotlinCompilerExtensionVersion

    val useLiveLiterals = composeOptions.useLiveLiterals

    // Create a project configuration that holds the androidx compose kotlin
    // compiler extension
    val kotlinExtension = project.configurations.create("kotlin-extension")
    project.dependencies
        .add(
            kotlinExtension.name, "androidx.compose.compiler:compiler:"
                    + (kotlinCompilerExtensionVersionInDsl
                ?: TaskManager.COMPOSE_KOTLIN_COMPILER_EXTENSION_VERSION))
    kotlinExtension.isTransitive = false
    kotlinExtension.description = "Configuration for Compose related kotlin compiler extension"

    extensions.configure<AndroidComponentsExtension<*, *, *>>("androidComponents") {
        onVariants {
            if (it.flavorName != flavor.name) return@onVariants

            val creationConfig = it as ComponentCreationConfig

            val taskNamePrefix = creationConfig.computeTaskName("compile")
            val possibleTaskNames = listOf(
                "${taskNamePrefix}Kotlin",
                "${taskNamePrefix}KotlinAndroid",
            )

            project.tasks.whenTaskAdded {
                if (name !in possibleTaskNames) return@whenTaskAdded

                addComposeArgsToKotlinCompile(
                    this as KotlinCompile,
                    creationConfig,
                    project.files(kotlinExtension),
                    useLiveLiterals,
                )
            }
        }
    }
}

private fun Project.setupAndroid(moduleNamespace: String) {

    android {
        namespace = moduleNamespace
        compileSdk = 33

        defaultConfig {
            minSdk = 16
            targetSdk = 33

            multiDexEnabled = true
            vectorDrawables.useSupportLibrary = true
        }

        buildFeatures {
            viewBinding = true
        }

        compileOptions {
            isCoreLibraryDesugaringEnabled = true
        }
        testOptions {
            unitTests.all {
                it.useJUnitPlatform()
            }
        }

        flavorDimensions += listOf("sdk")
        productFlavors {

            create("minApi16") {
                minSdk = 16
                dimension = "sdk"
            }

            create("minApi21") {
                minSdk = 21
                dimension = "sdk"
                addCompose(this) {
                    kotlinCompilerExtensionVersion = version("composeCompiler").toString()
                }
            }
        }
        dependencies {
            "minApi21Implementation"(bundle("compose"))
        }
    }
}

private fun Project.setupModule(scope: DependencyHandler) {
    apply(plugin = "kotlin-kapt")
    scope.apply {
        implementation(library("androidx-core"))
        implementation(library("appcompat"))
        implementation(library("fragment"))
        implementation(library("preference"))
        implementation(library("recycler"))
        implementation(library("material"))

        coreLibraryDesugaring(library("desugaring"))

        implementation(library("multidex"))
    }
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

fun Project.setupAndroidModule(
    moduleNamespace: String = "ru.art2000.calculator",
    dependencies: DependencyHandler = this.dependencies,
) {
    setupAndroid(moduleNamespace)
    setupModule(dependencies)
    apply(plugin = "com.google.dagger.hilt.android")
}

fun Project.setupKmmModule(androidPrefix: String = "ru.art2000.calculator") {

    kotlin {
        android()

        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach {
            it.binaries.framework {
                baseName = this@setupKmmModule.name
            }
        }

        sourceSets {
            val commonMain by getting
            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test-common"))
                    implementation(bundle("kotest-common"))
                }
            }
            val androidMain by getting {
                setupAndroid("$androidPrefix.${this@setupKmmModule.name}")
                setupModule(dependencies)
            }
            val androidUnitTest by getting {
                dependencies {
                    implementation(bundle("kotest-jvm"))
                }
            }
            val iosX64Main by getting
            val iosArm64Main by getting
            val iosSimulatorArm64Main by getting
            val iosMain by creating {
                dependsOn(commonMain)
                iosX64Main.dependsOn(this)
                iosArm64Main.dependsOn(this)
                iosSimulatorArm64Main.dependsOn(this)

                dependencies {
                    implementation(library("koin-core"))
                }
            }
            val iosX64Test by getting
            val iosArm64Test by getting
            val iosSimulatorArm64Test by getting
            val iosTest by creating {
                dependsOn(commonTest)
                iosX64Test.dependsOn(this)
                iosArm64Test.dependsOn(this)
                iosSimulatorArm64Test.dependsOn(this)
            }
        }
    }

    android {
        sourceSets {
            val main by getting
            main.manifest {
                srcFile("src/androidMain/AndroidManifest.xml")
            }
        }
    }
}

fun Project.setupFeatureModule() {
    apply(plugin = "com.google.dagger.hilt.android")
    setupKmmModule()
    kotlin {
        sourceSets {

            val commonMain by getting {
                dependencies {
                    implementation(project(":common"))
                    implementation(project(":extensions"))
                }
            }

            val androidMain by getting {
                dependencies {

                    implementation(project(":common"))
                    implementation(project(":extensions"))

                    implementation(bundle("hilt-impl"))
                    kapt(bundle("hilt-kapt"))

                    implementation(library("lifecycle-viewmodel"))
                    implementation(library("viewbindingdelegate"))
                }
            }
        }
    }
}

fun KotlinDependencyHandler.kapt(dependencyNotation: Any) =
    project.dependencies.add("kapt", dependencyNotation)

fun DependencyHandler.kapt(dependencyNotation: Any) =
    add("kapt", dependencyNotation)

//=========== Internal ============

internal fun Project.kotlin(configure: Action<KotlinMultiplatformExtension>) =
    extensions.configure("kotlin", configure)

internal fun KotlinMultiplatformExtension.sourceSets(configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>) =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("sourceSets", configure)

internal fun Project.android(configure: Action<InternalCommonExtension<*, *, DefaultConfig, *>>) =
    extensions.configure("android", configure)

internal inline val <T : Any, U : NamedDomainObjectCollection<out T>> U.getting
    get() = NamedDomainObjectCollectionDelegateProvider.of(this)

internal fun <T : Any, U : NamedDomainObjectCollection<T>> U.getting(configuration: T.() -> Unit) =
    NamedDomainObjectCollectionDelegateProvider.of(this, configuration)

internal fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? =
    add("implementation", dependencyNotation)

internal fun DependencyHandler.coreLibraryDesugaring(dependencyNotation: Any): Dependency? =
    add("coreLibraryDesugaring", dependencyNotation)
