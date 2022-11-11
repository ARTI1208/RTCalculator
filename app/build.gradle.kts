import com.android.build.api.dsl.VariantDimension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.hasProperty
import ru.art2000.modules.setupAndroidModule
import ru.art2000.modules.kapt
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
}

setupAndroidModule()

val composeVersion = "1.3.0"
val composeMaterial3Version = "1.0.0"
val composeCompilerVersion = "1.3.2"

val major = 1
val minor = 5
val patch = 1
val code = 15

android {
    signingConfigs {
        create("release") {

            val props = gradleLocalProperties(project.rootProject.projectDir)

            val fromProperties = props.hasProperty("signing.storeFile")

            fun stringProperty(key: String): String {
                return if (fromProperties) props.getProperty(key)
                else System.getenv(key.replace('.', '_'))
            }

            storeFile = File(stringProperty("signing.storeFile"))
            storePassword = stringProperty("signing.storePassword")
            keyAlias = stringProperty("signing.keyAlias")
            keyPassword = stringProperty("signing.keyPassword")
        }
    }

    defaultConfig {
        applicationId = "ru.art2000.calculator"
        versionCode = code

        versionName = "$major.$minor.$patch"

        buildConfigField("long", "BUILD_TIME", "${Date().time}")
        buildConfigField("boolean", "USE_COMPOSE", "true")

        setProperty("archivesBaseName", "RTCalculator-$versionName")
    }

    buildTypes {

        fun VariantDimension.withLocalProguard(vararg files: Any) {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules-common.pro", *files)
        }

        debug {
            isShrinkResources = false
            isMinifyEnabled = false
            withLocalProguard("proguard-rules-debug.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            withLocalProguard("proguard-rules-release.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    lint {
        checkReleaseBuilds = false
    }

    val javaVersion = JavaVersion.VERSION_1_8

    compileOptions {
        targetCompatibility = javaVersion
        sourceCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }

    val flavorsWithoutCompose = listOf("api16")

    // A bit of a hack. assembleApi21* tasks are generated and not accessible with getByName,
    // and trying to setup with tasks.whenTaskAdded and doFirst somewhy doesn't work
    // TODO Rework this somehow to support building all flavours at the same time
    if (
            gradle.startParameter.taskNames.isEmpty() ||
            gradle.startParameter.taskNames.all { task ->
                !flavorsWithoutCompose.any { task.contains(it, ignoreCase = true) }
            }
    ) {

        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = composeCompilerVersion
        }
    }
}

val newVersion = tasks.create("newVersion") {

    val version = "v${android.defaultConfig.versionName}"

    val resRoot = file("src/main/res")

    doLast {

        resRoot
            .walk()
            .onEnter {
                it == resRoot || it.name.startsWith("raw")
            }.forEach {
                if (it.name != "changelog.txt") return@forEach
                it.useLines { lines ->
                    val versionInChangelog = lines.first().takeWhile { c -> c != ' ' }
                    check(versionInChangelog == version) {
                        val parentName = it.parentFile.name
                        val fileName = it.name
                        "Version mismatch in build.gradle.kts and $parentName/$fileName"
                    }
                }
            }

        val taggingProcess = ProcessBuilder("git", "tag", version).start()
        taggingProcess.waitFor()
        check(taggingProcess.exitValue() == 0) {
            "Tagging result: ${taggingProcess.exitValue()}. Forgot to update version?"
        }

        val pushingProcess = ProcessBuilder("git", "push", "origin", version).start()
        pushingProcess.waitFor()
        check(pushingProcess.exitValue() == 0) {
            "Pushing result: ${pushingProcess.exitValue()}"
        }
    }
}

dependencies {
    implementation(fileTree("include" to listOf("*.jar"), "dir" to "libs"))
    implementation(project(":extensions"))
    implementation(project(":currency"))
    implementation(project(":calculator"))
    implementation(project(":unit"))
    implementation(project(":common"))

    implementation(libs.constraintlayout)
    implementation(libs.bundles.hilt.impl)
    kapt(libs.bundles.hilt.kapt)
    implementation(libs.lifecycle.viewmodel)

    implementation(libs.viewbinding.delegate)

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")

    "minApi21Implementation"("androidx.activity:activity-compose:1.6.1")

    "minApi21Implementation"("androidx.compose.ui:ui:$composeVersion")
    // Tooling support (Previews, etc.)
    "minApi21Implementation"("androidx.compose.ui:ui-tooling:$composeVersion")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    "minApi21Implementation"("androidx.compose.foundation:foundation:$composeVersion")
    // Material Design
    "minApi21Implementation"("androidx.compose.material:material:$composeVersion")
    "minApi21Implementation"("androidx.compose.material3:material3:$composeMaterial3Version")
    // Material design icons
    "minApi21Implementation"("androidx.compose.material:material-icons-core:$composeVersion")
    "minApi21Implementation"("androidx.compose.material:material-icons-extended:$composeVersion")
    // Integration with observables
    "minApi21Implementation"("androidx.compose.runtime:runtime-livedata:$composeVersion")
    "minApi21Implementation"("androidx.compose.compiler:compiler:$composeCompilerVersion")

}