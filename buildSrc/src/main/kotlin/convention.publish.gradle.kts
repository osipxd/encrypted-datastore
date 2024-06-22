import com.redmadrobot.build.dsl.developer
import com.redmadrobot.build.dsl.mit
import com.redmadrobot.build.dsl.setGitHubProject
import com.vanniktech.maven.publish.SonatypeHost

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
    val component = components["java"] as AdhocComponentWithVariants
    component.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
    component.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

    // Workaround to not publish test fixtures sources added by com.vanniktech.maven.publish plugin
    // TODO: Remove as soon as https://github.com/vanniktech/gradle-maven-publish-plugin/issues/779 closed
    afterEvaluate {
        component.withVariantsFromConfiguration(configurations["testFixturesSourcesElements"]) { skip() }
    }
}
