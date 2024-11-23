import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

with(kotlinExtension) {
    jvmToolchain(11)

    explicitApi()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    "api"(platform(project(":encrypted-datastore-bom")))
}
