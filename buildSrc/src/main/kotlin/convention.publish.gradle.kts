import com.redmadrobot.build.dsl.*
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
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

apiValidation {
    ignoredPackages.add("io.github.osipxd.datastore.encrypted.internal")
    nonPublicMarkers.add("androidx.annotation.RestrictTo")

    // Check only the project to which BCV is applied
    ignoredProjects.addAll(subprojects.map { it.name })
}
