import com.redmadrobot.build.dsl.*

plugins {
    id("redmadrobot.root-project") version "0.12.1"
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
