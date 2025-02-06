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
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(artifactId = project.name)

    pom {
        name = project.name
        description = "Extensions to store DataStore in EncryptedFile"

        setGitHubProject("dayanruben/encrypted-datastore")
        licenses {
            mit()
        }
        developers {
            developer(id = "osipxd", name = "Osip Fatkullin", email = "osip.fatkullin@gmail.com")
            developer(id = "dayanruben", name = "Dayan Ruben", email = "mail@dayanruben.com")
        }
    }
}

apiValidation {
    ignoredPackages.add("com.dayanruben.datastore.encrypted.internal")
    nonPublicMarkers.add("androidx.annotation.RestrictTo")

    // Check only the project to which BCV is applied
    ignoredProjects.addAll(subprojects.map { it.name })
}
