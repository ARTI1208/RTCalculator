import com.android.build.gradle.internal.dsl.DefaultConfig
import com.android.build.gradle.internal.dsl.InternalCommonExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    id("com.android.base")
}

val libs = the<LibrariesForLibs>()
val javaVersion = JavaVersion.toVersion(libs.versions.java.get())

android {

    namespace = "ru.art2000.calculator.${project.name}"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

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
    val implementationConfigurationName =
        when (val extension = kotlin as? KotlinMultiplatformExtension) {
            null -> "implementation"
            else -> extension.sourceSets.getByName("androidMain").implementationConfigurationName
        }

    fun unifiedImplementation(
        dependencyNotation: Provider<MinimalExternalModuleDependency>,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
    ) {
        addProvider(implementationConfigurationName, dependencyNotation, dependencyConfiguration)
    }

    unifiedImplementation(libs.androidx.core)
    unifiedImplementation(libs.appcompat)
    unifiedImplementation(libs.fragment)
    unifiedImplementation(libs.preference)
    unifiedImplementation(libs.recycler)
    unifiedImplementation(libs.material)

    "coreLibraryDesugaring"(libs.desugaring)
}

configurations {
    val runtimeConfigurationNames = names.filter { it.contains("runtime", ignoreCase = true) }
    runtimeConfigurationNames.forEach {
        named(it) {
            exclude(group = "androidx.profileinstaller", module = "profileinstaller")
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// ===== Utils =====================================================================================

fun android(configure: Action<InternalCommonExtension<*, *, DefaultConfig, *, *, *>>) =
    extensions.configure("android", configure)

val Project.kotlin: KotlinProjectExtension
    get() = extensions.getByName("kotlin") as KotlinProjectExtension

fun <T : KotlinProjectExtension> Project.kotlin(configure: Action<T>) =
    extensions.configure("kotlin", configure)