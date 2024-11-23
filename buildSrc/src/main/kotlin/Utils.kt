import org.gradle.api.NamedDomainObjectProvider
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun NamedDomainObjectProvider<KotlinSourceSet>.dependencies(
    handler: KotlinDependencyHandler.() -> Unit,
): Unit = configure { dependencies(handler) }
