buildscript {
    val kotlinVersion = "1.6.10"

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    tasks.withType<JavaCompile> {
        options.compilerArgs.plusAssign(listOf(
            "-Xlint:unchecked", "-Xlint:deprecation"
        ))
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
