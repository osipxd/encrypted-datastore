import com.redmadrobot.build.dsl.*

plugins {
    com.redmadrobot.`publish-config`
}

group = "io.github.osipxd"
version = "${libs.versions.datastore.get()}-alpha02"

redmadrobot {
    publishing {
        signArtifacts.set(true)

        pom {
            setGitHubProject("osipxd/encrypted-datastore")
            licenses {
                mit()
            }
            developers {
                developer(id = "osipxd", name = "Osip Fatkullin", email = "osip.fatkullin@gmail.com")
            }
        }
    }
}
