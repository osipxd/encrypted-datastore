import internal.libs
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    jvm()
    androidTarget()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("commonJvm") {
                withAndroidTarget()
                withJvm()
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project.dependencies.platform(project(":encrypted-datastore-bom")))
        }
        named("commonJvmTest").dependencies {
            implementation(kotlin("test", version = libs.versions.kotlin.get()))
            implementation(project.dependencies.platform(libs.junit.bom))
            implementation(libs.junit.jupiter)
        }
    }
}

applyKotlinDefaults()
android.applyAndroidDefaults()
