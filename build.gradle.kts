plugins {
    com.redmadrobot.`android-config`
}

val datastoreVersion = libs.versions.datastore.get()
allprojects {
    group = "io.github.osipxd"
    version = "$datastoreVersion-beta03"
}

redmadrobot {
    // Min SDK should be aligned with min SDK in androidx.security:security-crypto
    android.minSdk = 23
}
