dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val lifecycle = version("lifecycle", "2.6.0")
            val androidxHilt = version("androidxHilt", "1.0.0")
            val daggerHilt = version("daggerHilt", "2.45")
            val room = version("room", "2.5.0")
            val kotest = version("kotest", "5.5.4")
            val activity = version("activity", "1.6.1")
            val compose = version("compose", "1.3.1")
            val composeCompiler = version("composeCompiler", "1.4.3")
            val composeMaterial3 = version("composeMaterial3", "1.0.1")
            val accompanist = version("accompanist", "0.28.0")
            val koin = version("koin", "3.3.3")
            val work = version("work", "2.8.0")
            val swiperefreshlayout = version("swiperefreshlayout", "1.1.0")
            val okhttpMinApi16 = version("okhttpMinApi16", "3.12.13")
            val okhttpMinApi21 = version("okhttpMinApi21", "4.10.0")
            val okhttpVersions = listOf(
                okhttpMinApi16,
                okhttpMinApi21,
            )
            val retrofit = version("retrofit", "2.9.0")
            val leakcanary = version("leakcanary", "2.10")
            val commonsMath = version("commonsMath", "3.6.1")
            val slidingUpPanel = version("slidingUpPanel", "4.5.0")
            val multiplatformSettings = version("multiplatformSettings", "1.0.0")

            library("desugaring", "com.android.tools:desugar_jdk_libs:2.0.3")
            library("appcompat", "androidx.appcompat:appcompat:1.6.1")
            library("androidx.core", "androidx.core:core-ktx:1.9.0")
            library("preference", "androidx.preference:preference-ktx:1.2.0")
            library("fragment", "androidx.fragment:fragment-ktx:1.5.5")
            library("recycler", "androidx.recyclerview:recyclerview:1.3.0")
            library("material", "com.google.android.material:material:1.8.0")

            listOf("lifecycle-process", "lifecycle-viewmodel-ktx").forEach {
                library(it.removeSuffix("-ktx"), "androidx.lifecycle", it)
                    .versionRef(lifecycle)
            }

            library("viewbindingdelegate", "com.github.kirich1409:viewbindingpropertydelegate-full:1.5.8")

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

            val composeAliases = mutableListOf<String>()

            listOf(
                "androidx.compose.ui:ui",
                "androidx.compose.ui:ui-tooling",
                "androidx.compose.foundation:foundation",
                "androidx.compose.material:material",
            ).forEach {
                val (group, artifact) = it.split(':')
                val alias = "compose-$artifact"
                composeAliases += alias
                library(alias, group, artifact).versionRef(compose)
            }

            library("compose-activity".also { composeAliases += it }, "androidx.activity", "activity-compose")
                .versionRef(activity)

            library("compose-compiler".also { composeAliases += it }, "androidx.compose.compiler", "compiler")
                .versionRef(composeCompiler)

            library("compose-material3".also { composeAliases += it }, "androidx.compose.material3", "material3")
                .versionRef(composeMaterial3)

            library("compose-theme-adapter3".also { composeAliases += it }, "com.google.accompanist", "accompanist-themeadapter-material3")
                .versionRef(accompanist)

            bundle("compose", composeAliases)

            library("koin-core", "io.insert-koin", "koin-core").versionRef(koin)
            library("work", "androidx.work", "work-runtime-ktx").versionRef(work)
            library("swiperefreshlayout", "androidx.swiperefreshlayout", "swiperefreshlayout")
                .versionRef(swiperefreshlayout)

            fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int = if (this is Collection<*>) this.size else default

            fun <T, R, V> Iterable<T>.zipAll(other: Iterable<R>, transform: (a: T, b: R) -> V): List<V> {
                val first = iterator()
                val list = ArrayList<V>(collectionSizeOrDefault(10) + other.collectionSizeOrDefault(10))
                while (first.hasNext()) {
                    val firstElem = first.next()
                    val second = other.iterator()
                    while (second.hasNext()) {
                        list.add(transform(firstElem, second.next()))
                    }
                }
                return list
            }

            infix fun <T, R> Iterable<T>.zipAll(other: Iterable<R>): List<Pair<T, R>> {
                return zipAll(other) { t1, t2 -> t1 to t2 }
            }

            listOf(
                "okhttp",
            ).zipAll(okhttpVersions).forEach { (artifact, version) ->
                val type = version.substringAfter("okhttp")
                val alias = "okhttp-${artifact.replace("-", "")}$type"
                library(alias, "com.squareup.okhttp3", artifact).versionRef(version)
            }

            listOf(
                "retrofit",
                "converter-simplexml",
            ).forEach { artifact ->
                val alias = "retrofit-${artifact.replace("-", "")}"
                library(alias, "com.squareup.retrofit2", artifact).versionRef(retrofit)
            }

            library("leakcanary", "com.squareup.leakcanary", "leakcanary-android")
                .versionRef(leakcanary)

            library("apache-commons-math3", "org.apache.commons", "commons-math3")
                .versionRef(commonsMath)

            library("slidingUpPanel", "com.github.hannesa2", "AndroidSlidingUpPanel")
                .versionRef(slidingUpPanel)

            library("multiplatformSettings", "com.russhwolf", "multiplatform-settings")
                .versionRef(multiplatformSettings)
        }
    }
}

include(":app")
include(":currency")
include(":calculator")
include(":unit")
include(":common")
include(":extensions")
include(":shared")
include(":settings")
