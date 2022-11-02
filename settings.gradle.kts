dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.7.20")
            version("lifecycle", "2.5.1")
            library("desugaring", "com.android.tools:desugar_jdk_libs:1.2.2")
            library("appcompat", "androidx.appcompat:appcompat:1.5.1")
            library("androidx.core", "androidx.core:core-ktx:1.9.0")
            library("preference", "androidx.preference:preference-ktx:1.2.0")
            library("fragment", "androidx.fragment:fragment-ktx:1.5.4")
            library("recycler", "androidx.recyclerview:recyclerview:1.2.1")
            library("material", "com.google.android.material:material:1.7.0")
            library("lifecycle-process", "androidx.lifecycle", "lifecycle-process")
                .versionRef("lifecycle")
            library("lifecycle-viewmodel", "androidx.lifecycle", "lifecycle-viewmodel")
                .versionRef("lifecycle")

            library("junit", "junit:junit:4.13.2")
        }
    }
}

include(":app", ":extensions")