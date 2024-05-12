import com.redmadrobot.build.dsl.developer
import com.redmadrobot.build.dsl.mit
import com.redmadrobot.build.dsl.setGitHubProject
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.plugins.internal.JavaPluginHelper
import org.gradle.internal.component.external.model.TestFixturesSupport
import org.gradle.jvm.component.internal.DefaultJvmSoftwareComponent

plugins {
    com.vanniktech.maven.publish
    signing
}

signing {
    useGpgCmd()
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()

    coordinates(artifactId = project.name)

    pom {
        name = project.name
        description = project.description

        setGitHubProject("osipxd/encrypted-datastore")
        licenses {
            mit()
        }
        developers {
            developer(id = "osipxd", name = "Osip Fatkullin", email = "osip.fatkullin@gmail.com")
        }
    }
}

// Exclude test fixtures from publication, as we use it only internally
plugins.withId("org.gradle.java-test-fixtures") {
    val component = JavaPluginHelper.getJavaComponent(project) as DefaultJvmSoftwareComponent
    val feature = component.features.getByName(TestFixturesSupport.TEST_FIXTURES_FEATURE_NAME)
    component.withVariantsFromConfiguration(feature.apiElementsConfiguration) { skip() }
    component.withVariantsFromConfiguration(feature.runtimeElementsConfiguration) { skip() }

    // Workaround to not publish test fixtures sources added by com.vanniktech.maven.publish plugin
    // TODO: Remove as soon as https://github.com/vanniktech/gradle-maven-publish-plugin/issues/779 closed
    afterEvaluate {
        val configuration = project.configurations[feature.sourceSet.sourcesElementsConfigurationName]
        component.withVariantsFromConfiguration(configuration) { skip() }
    }
}
