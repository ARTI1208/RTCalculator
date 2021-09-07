import com.android.build.api.dsl.VariantDimension
import java.util.*
import java.text.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdkVersion = "android-31"
    buildToolsVersion = "31.0.0"
    defaultConfig {
        applicationId = "ru.art2000.calculator"
        minSdk = 18
        targetSdk = 31
        versionCode = 7

        val major = 1
        val minor = 1
        val patch = 5

        versionName = "$major.$minor.$patch"

        multiDexEnabled = true
        buildConfigField("String", "BUILD_DATE", '"' + getBuildDate() +'"')

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
        isCheckReleaseBuilds = false
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

fun getBuildDate() = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date())

val kotlinVersion = "1.5.30"

dependencies {
    android.defaultConfig.vectorDrawables.useSupportLibrary = true
    implementation(fileTree("include" to listOf("*.jar"), "dir" to "libs"))
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.sothree.slidinguppanel:library:3.4.0")
    implementation("org.apache.commons:commons-math3:3.6.1")

    val roomVersion = "2.3.0"

    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("io.reactivex.rxjava3:rxjava:3.0.13") // 3.1+ requires minApi 21+
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")


    val multidexVersion = "2.0.1"
    implementation("androidx.multidex:multidex:$multidexVersion")


    val okhttpVersion = "3.12.12"
    //noinspection GradleDependency
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    //noinspection GradleDependency
    implementation("com.squareup.okhttp3:okhttp-urlconnection:$okhttpVersion")

    val retrofitVersion = "2.6.0"
    //noinspection GradleDependency
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    //noinspection GradleDependency
    implementation("com.squareup.retrofit2:converter-simplexml:$retrofitVersion") {
        exclude(group = "stax", module = "stax-api")
        exclude(group = "stax", module = "stax")
        exclude(group = "xpp3", module = "xpp3")
    }

    testImplementation("junit:junit:4.13.2")
}