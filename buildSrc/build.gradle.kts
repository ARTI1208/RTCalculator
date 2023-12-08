plugins {
    `kotlin-dsl`
    java
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.ksp.gradle)
    implementation(libs.dagger.gradle)
//    compileClasspath(libs.dagger.gradle)
//    kotlinCompilerClasspath(libs.dagger.gradle)
    implementation(libs.javapoet)
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

repositories {
    mavenCentral()
    google()
}

sourceSets.main.get().java.srcDir("src/main/kotlin")