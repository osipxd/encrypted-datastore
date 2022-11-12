import com.redmadrobot.build.dsl.*

plugins {
    id("com.redmadrobot.publish-config") version "0.17"
}

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
