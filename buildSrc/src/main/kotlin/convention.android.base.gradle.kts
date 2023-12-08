import com.android.build.api.dsl.ComposeOptions
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.TaskManager
import com.android.build.gradle.internal.component.ComponentCreationConfig
import com.android.build.gradle.internal.dsl.DefaultConfig
import com.android.build.gradle.internal.dsl.InternalCommonExtension
import com.android.build.gradle.internal.utils.addComposeArgsToKotlinCompile
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.base")
}

val libs = the<LibrariesForLibs>()
val javaVersion = JavaVersion.toVersion(libs.versions.java.get())

android {

    namespace = "ru.art2000.calculator.${project.name}"
    compileSdk = 34

    defaultConfig {
        minSdk = 16
        targetSdk = 34

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
                kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
            }
        }
    }
    compileOptions {
        targetCompatibility = javaVersion
        sourceCompatibility = javaVersion
    }
}

kotlin<KotlinProjectExtension> {
    jvmToolchain(javaVersion.majorVersion.toInt())

    // Nevertheless it is applied in kmp convention plugin, code below is run before
    // and we need android source set to add dependencies in this plugin
    if (this is KotlinMultiplatformExtension) {
        androidTarget()
    }
}

dependencies {
    val implementationConfigurationName = when (val extension = kotlin as? KotlinMultiplatformExtension) {
        null -> "implementation"
        else -> extension.sourceSets.getByName("androidMain").implementationConfigurationName
    }

    fun unifiedImplementation(dependencyNotation: Any) {
        add(implementationConfigurationName, dependencyNotation)
    }

    unifiedImplementation(libs.androidx.core)
    unifiedImplementation(libs.appcompat)
    unifiedImplementation(libs.fragment)
    unifiedImplementation(libs.preference)
    unifiedImplementation(libs.recycler)
    unifiedImplementation(libs.material)
    unifiedImplementation(libs.multidex)

    "coreLibraryDesugaring"(libs.desugaring)
    "minApi21Implementation"(libs.bundles.compose)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// ===== Utils =====================================================================================

fun Project.addCompose(flavor: ProductFlavor, options: ComposeOptions.() -> Unit = {}) {

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

            project.tasks.configureEach {
                if (name !in possibleTaskNames) return@configureEach

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

fun android(configure: Action<InternalCommonExtension<*, *, DefaultConfig, *, *>>) =
    extensions.configure("android", configure)

val Project.kotlin: KotlinProjectExtension
    get() = extensions.getByName("kotlin") as KotlinProjectExtension

fun <T : KotlinProjectExtension> Project.kotlin(configure: Action<T>) =
    extensions.configure("kotlin", configure)