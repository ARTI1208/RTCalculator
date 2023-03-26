@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.PostProcessing
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

val major = 1
val minor = 5
val patch = 4
val code = 18

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

        setProperty("archivesBaseName", "RTCalculator-$versionName")
    }

    buildTypes {

        fun withCommonProguard(files: Array<out Any>) = arrayOf("proguard-rules-common.pro", *files)

        fun VariantDimension.withLocalProguard(vararg files: Any) {
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                *withCommonProguard(files),
            )
        }

        fun PostProcessing.withLocalProguard(vararg files: Any) {
            proguardFiles(*withCommonProguard(files))
        }

        debug {
            isShrinkResources = false
            isMinifyEnabled = false
            withLocalProguard("proguard-rules-debug.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        release {

            postprocessing {
                isRemoveUnusedCode = true
                isRemoveUnusedResources = true
                isObfuscate = true
                isOptimizeCode = true
                withLocalProguard("proguard-rules-release.pro")
            }

            signingConfig = signingConfigs.getByName("release")
        }
    }
    lint {
        checkReleaseBuilds = false
    }
}

val newVersion = tasks.create("newVersion") {

    val version = "v${android.defaultConfig.versionName}"

    val resRoot = file("../settings/src/androidMain/res")

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
    implementation(project(":shared"))

    implementation(libs.constraintlayout)
    implementation(libs.bundles.hilt.impl)
    kapt(libs.bundles.hilt.kapt)
    implementation(libs.lifecycle.viewmodel)

    implementation(libs.viewbindingdelegate)

    debugImplementation(libs.leakcanary)

    "minApi21Implementation"(libs.bundles.compose)

}