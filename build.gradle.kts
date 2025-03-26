
val datastoreVersion = libs.versions.androidx.datastore.get()
val libVersion = libs.versions.encrypted.datastore.get()
allprojects {
    group = "com.dayanruben"
    version = "$datastoreVersion-$libVersion"
}
