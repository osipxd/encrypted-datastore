import com.redmadrobot.build.dsl.developer
import com.redmadrobot.build.dsl.mit
import com.redmadrobot.build.dsl.setGitHubProject

plugins {
    com.redmadrobot.`publish-config`
    com.redmadrobot.`android-config`
}

val datastoreVersion = libs.versions.datastore.get()
subprojects {
    group = "io.github.osipxd"
    version = "$datastoreVersion-alpha02"
}

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
