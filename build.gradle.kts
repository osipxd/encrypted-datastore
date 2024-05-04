import com.redmadrobot.build.dsl.developer
import com.redmadrobot.build.dsl.mit
import com.redmadrobot.build.dsl.setGitHubProject

plugins {
    com.redmadrobot.`publish-config`
    com.redmadrobot.`android-config`
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl = uri("https://s01.oss.sonatype.org/service/local/")
            snapshotRepositoryUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

val datastoreVersion = libs.versions.datastore.get()
allprojects {
    group = "io.github.osipxd"
    version = "$datastoreVersion-beta01"
}

redmadrobot {
    // Min SDK should be aligned with min SDK in androidx.security:security-crypto
    android.minSdk = 23

    publishing {
        signArtifacts = true

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
