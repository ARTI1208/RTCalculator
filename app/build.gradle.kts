import com.android.build.api.dsl.VariantDimension
import java.util.*
import java.text.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

val composeVersion = "1.1.1"
val composeCompilerVersion = composeVersion

android {
    compileSdkVersion = "android-31"
    buildToolsVersion = "32.0.0"
    defaultConfig {
        applicationId = "ru.art2000.calculator"
        minSdk = 16
        targetSdk = 31
        versionCode = 11

        val major = 1
        val minor = 3
        val patch = 1

        versionName = "$major.$minor.$patch"

        multiDexEnabled = true
        buildConfigField("String", "BUILD_DATE", '"' + getBuildDate() +'"')
        buildConfigField("boolean", "USE_COMPOSE", "true")

        setProperty("archivesBaseName", "RTCalculator-$versionName")
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    buildTypes {

        fun VariantDimension.withLocalProguard(vararg files: Any) {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules-common.pro", *files)
        }

        debug {
            isShrinkResources = false
            isMinifyEnabled = false
            withLocalProguard("proguard-rules-debug.pro")
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            withLocalProguard("proguard-rules-release.pro")
        }
    }
    lint {
        checkReleaseBuilds = false
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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

fun getBuildDate() = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date())

val kotlinVersion = "1.6.10"

dependencies {
    android.defaultConfig.vectorDrawables.useSupportLibrary = true
    implementation(fileTree("include" to listOf("*.jar"), "dir" to "libs"))
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("com.sothree.slidinguppanel:library:3.4.0")
    implementation("org.apache.commons:commons-math3:3.6.1")

    val roomVersion = "2.4.2"

    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")


    val multidexVersion = "2.0.1"
    implementation("androidx.multidex:multidex:$multidexVersion")


    val okhttpVersionMinApi16 = "3.12.13"
    //noinspection GradleDependency
    "minApi16Implementation"("com.squareup.okhttp3:okhttp:$okhttpVersionMinApi16")
    //noinspection GradleDependency
    "minApi16Implementation"("com.squareup.okhttp3:okhttp-urlconnection:$okhttpVersionMinApi16")

    val okhttpVersionMinApi21 = "4.9.3"
    "minApi21Implementation"("com.squareup.okhttp3:okhttp:$okhttpVersionMinApi21")
    "minApi21Implementation"("com.squareup.okhttp3:okhttp-urlconnection:$okhttpVersionMinApi21")

    val retrofitVersionMinApi16 = "2.6.4"
    //noinspection GradleDependency
    "minApi16Implementation"("com.squareup.retrofit2:retrofit:$retrofitVersionMinApi16")
    //noinspection GradleDependency
    "minApi16Implementation"("com.squareup.retrofit2:converter-simplexml:$retrofitVersionMinApi16") {
        exclude(group = "stax", module = "stax-api")
        exclude(group = "stax", module = "stax")
        exclude(group = "xpp3", module = "xpp3")
    }

    val retrofitVersionMinApi21 = "2.9.0"
    "minApi21Implementation"("com.squareup.retrofit2:retrofit:$retrofitVersionMinApi21")
    "minApi21Implementation"("com.squareup.retrofit2:converter-simplexml:$retrofitVersionMinApi21") {
        exclude(group = "stax", module = "stax-api")
        exclude(group = "stax", module = "stax")
        exclude(group = "xpp3", module = "xpp3")
    }

    "minApi21Implementation"("androidx.compose.ui:ui:$composeVersion")
    // Tooling support (Previews, etc.)
    "minApi21Implementation"("androidx.compose.ui:ui-tooling:$composeVersion")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    "minApi21Implementation"("androidx.compose.foundation:foundation:$composeVersion")
    // Material Design
    "minApi21Implementation"("androidx.compose.material:material:$composeVersion")
    // Material design icons
    "minApi21Implementation"("androidx.compose.material:material-icons-core:$composeVersion")
    "minApi21Implementation"("androidx.compose.material:material-icons-extended:$composeVersion")
    // Integration with observables
    "minApi21Implementation"("androidx.compose.runtime:runtime-livedata:$composeVersion")
    "minApi21Implementation"("androidx.compose.compiler:compiler:$composeCompilerVersion")

    "minApi21Implementation"("androidx.activity:activity-compose:1.4.0")

    testImplementation("junit:junit:4.13.2")
}