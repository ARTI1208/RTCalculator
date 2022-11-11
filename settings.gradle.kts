dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("libs") {
            val lifecycle = version("lifecycle", "2.5.1")
            val androidxHilt = version("androidxHilt", "1.0.0")
            val daggerHilt = version("daggerHilt", "2.44")
            val room = version("room", "2.4.3")
            val kotest = version("kotest", "5.5.4")

            library("desugaring", "com.android.tools:desugar_jdk_libs:1.2.2")
            library("appcompat", "androidx.appcompat:appcompat:1.5.1")
            library("androidx.core", "androidx.core:core-ktx:1.9.0")
            library("preference", "androidx.preference:preference-ktx:1.2.0")
            library("fragment", "androidx.fragment:fragment-ktx:1.5.4")
            library("recycler", "androidx.recyclerview:recyclerview:1.2.1")
            library("material", "com.google.android.material:material:1.7.0")

            listOf("lifecycle-process", "lifecycle-viewmodel-ktx").forEach {
                library(it.removeSuffix("-ktx"), "androidx.lifecycle", it)
                    .versionRef(lifecycle)
            }

            library("viewbinding-delegate", "com.github.kirich1409:viewbindingpropertydelegate:1.5.6")

            listOf("hilt-work", "hilt-compiler").forEach {
                library(it, "androidx.hilt", it).versionRef(androidxHilt)
            }

            listOf("hilt-android", "hilt-android-compiler").forEach {
                library(it, "com.google.dagger", it).versionRef(daggerHilt)
            }

            bundle("hilt-impl", listOf("hilt-work", "hilt-android"))
            bundle("hilt-kapt", listOf("hilt-compiler", "hilt-android-compiler"))

            listOf("room-compiler", "room-ktx", "room-runtime").forEach {
                library(it.removeSuffix("-ktx"), "androidx.room", it).versionRef(room)
            }

            bundle("room-impl", listOf("room", "room-runtime"))
            bundle("room-kapt", listOf("room-compiler"))

            library("datetime", "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            library("coroutines-core", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

            library("multidex", "androidx.multidex:multidex:2.0.1")

            library("constraintlayout", "androidx.constraintlayout:constraintlayout:2.1.4")

            listOf(
                "kotest-framework-engine",
                "kotest-assertions-core",
                "kotest-property",
                "kotest-framework-datatest",
                "kotest-runner-junit5",
            ).forEach {
                library(it, "io.kotest", it).versionRef(kotest)
            }

            bundle("kotest-common", listOf(
                "kotest-framework-engine",
                "kotest-assertions-core",
                "kotest-property",
                "kotest-framework-datatest",
            ))
            
            bundle("kotest-jvm", listOf("kotest-runner-junit5"))

            plugin("dagger", "com.google.dagger.hilt.android").versionRef(daggerHilt)
        }
    }
}

include(":app")
include(":currency")
include(":calculator")
include(":unit")
include(":common")
include(":extensions")
