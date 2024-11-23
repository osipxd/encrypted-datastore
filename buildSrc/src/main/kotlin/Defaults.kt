import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

internal fun Project.applyKotlinDefaults() {
    with(kotlinExtension) {
        jvmToolchain(11)
        explicitApi()
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

internal fun LibraryExtension.applyAndroidDefaults() {
    compileSdk = 35

    // Min SDK should be aligned with min SDK in androidx.security:security-crypto
    defaultConfig.minSdk = 23

    buildFeatures {
        resValues = false
        androidResources = false
        shaders = false
    }

    lint {
        checkDependencies = true
        abortOnError = true
        warningsAsErrors = true
    }
}
