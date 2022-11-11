plugins {
    `kotlin-dsl`
}

fun library(alias: String) =
    extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findLibrary(alias)
        .get()

dependencies {
    implementation(library("android-gradle"))
    implementation(library("kotlin-gradle"))
    implementation("com.squareup:javapoet:1.13.0")
}

repositories {
    mavenCentral()
    google()
}

sourceSets.main.get().java.srcDir("src/main/kotlin")