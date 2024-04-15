package internal

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension

internal fun ExtensionAware.java(configure: JavaPluginExtension.() -> Unit) {
    extensions.configure<JavaPluginExtension>("java", configure)
}