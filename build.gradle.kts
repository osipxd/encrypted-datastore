import com.redmadrobot.build.dsl.*

plugins {
    com.redmadrobot.`publish-config`
    com.redmadrobot.`android-config`
}

group = "io.github.osipxd"
version = "${libs.versions.datastore.get()}-alpha02"

redmadrobot {
    // Min SDK should be aligned with min SDK in androidx.security:security-crypto
    android.minSdk.set(21)

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
