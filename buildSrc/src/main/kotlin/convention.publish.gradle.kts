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

apiValidation {
    ignoredPackages.add("io.github.osipxd.datastore.encrypted.internal")
    nonPublicMarkers.add("androidx.annotation.RestrictTo")

    // Check only the project to which BCV is applied
    ignoredProjects.addAll(subprojects.map { it.name })
}
