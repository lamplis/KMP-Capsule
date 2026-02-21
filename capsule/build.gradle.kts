import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.2.20"
    alias(libs.plugins.androidMultiplatformLibrary)
    id("org.jetbrains.compose") version "1.9.3"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
}

kotlin {
    androidLibrary {
        namespace = "com.amp_digital.capsule"
        compileSdk = 36
        minSdk = 21
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Capsule"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
        }
        androidMain {
            kotlin.srcDir("src/main/java")
            dependencies {
                // Local Kyant Capsule implementation included via src/main/java
            }
        }
        iosMain.dependencies {
            // iOS-specific dependencies if needed
        }
    }
}
