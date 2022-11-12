import com.redmadrobot.build.dsl.*

plugins {
    alias(libs.plugins.rmr.kotlinLibrary) apply false
    alias(libs.plugins.rmr.publish.config)
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
