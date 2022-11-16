import com.redmadrobot.build.dsl.ossrh

plugins {
    id("com.redmadrobot.kotlin-library")
    id("com.redmadrobot.publish")
}

publishing {
    repositories {
        ossrh { credentials(PasswordCredentials::class) }
    }
}
