import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.lint.*

plugins {
    id("convention.library.kmp") apply false
}

plugins.withId("convention.library.kmp") {
    val testFixturesProject = project("testFixtures")
    val thisProject = project

    with(testFixturesProject) {
        plugins.apply("convention.library.kmp")

        androidComponents.finalizeDsl {
            val parentAndroid = thisProject.extensions.getByName<CommonExtension<*, *, *, *, *, *>>("android")
            val parentNamespace = checkNotNull(parentAndroid.namespace) {
                "Please specify 'android.namespace' in the project '${thisProject.path}'"
            }
            it.namespace = "$parentNamespace.testFixtures"
        }

        // Disable most of the Lint tasks for the test fixtures project
        tasks.withType<AndroidLintAnalysisTask>().configureEach { enabled = false }
        tasks.withType<AndroidLintTask>().configureEach { enabled = false }
        tasks.withType<AndroidLintTextOutputTask>().configureEach { enabled = false }
        tasks.withType<LintModelWriterTask>().configureEach { enabled = false }

        kotlin {
            explicitApi = null

            sourceSets {
                commonMain.dependencies {
                    implementation(thisProject)
                }
            }
        }
    }

    kotlin {
        sourceSets {
            commonTest.dependencies {
                implementation(testFixturesProject)
            }

            androidInstrumentedTest.dependencies {
                implementation(testFixturesProject)
            }
        }
    }
}
