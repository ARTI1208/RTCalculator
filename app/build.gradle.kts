import com.android.build.api.dsl.VariantDimension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.hasProperty
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

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
    compileSdkVersion = "android-33"
    buildToolsVersion = "33.0.0"

    namespace = "ru.art2000.calculator"

    defaultConfig {
        applicationId = "ru.art2000.calculator"
        minSdk = 16
        targetSdk = 33
        versionCode = code

        versionName = "$major.$minor.$patch"

        multiDexEnabled = true
        buildConfigField("long", "BUILD_TIME", "${Date().time}")
        buildConfigField("boolean", "USE_COMPOSE", "true")

        setProperty("archivesBaseName", "RTCalculator-$versionName")
    }

    buildFeatures {
        viewBinding = true
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = javaVersion.toString()
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
        }
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

tasks.whenTaskAdded {
    if (name.startsWith("test") && name.endsWith("UnitTest") && project.hasProperty("excludeTime")) {
        (this as Test).exclude {
            it.name.contains("TimeTest")
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

val kotlinVersion = libs.versions.kotlin.get()

dependencies {
    android.defaultConfig.vectorDrawables.useSupportLibrary = true
    implementation(fileTree("include" to listOf("*.jar"), "dir" to "libs"))
    implementation(project(":extensions"))

    val androidxHiltVersion = "1.0.0"
    val daggerHiltVersion = "2.44"
    val multidexVersion = "2.0.1"
    val roomVersion = "2.4.3"

    implementation(libs.appcompat)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.androidx.core)
    implementation(libs.preference)
    implementation("androidx.hilt:hilt-work:$androidxHiltVersion")
    kapt("androidx.hilt:hilt-compiler:$androidxHiltVersion")
    implementation(libs.lifecycle.viewmodel)
    implementation("androidx.multidex:multidex:$multidexVersion")
    implementation(libs.preference)
    implementation(libs.recycler)
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    coreLibraryDesugaring(libs.desugaring)

    implementation("com.github.hannesa2:AndroidSlidingUpPanel:4.5.0")
    implementation("com.github.kirich1409:viewbindingpropertydelegate:1.5.6")

    implementation(libs.material)
    implementation("com.google.dagger:hilt-android:$daggerHiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$daggerHiltVersion")

    implementation("org.apache.commons:commons-math3:3.6.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")

    testImplementation(libs.junit)



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