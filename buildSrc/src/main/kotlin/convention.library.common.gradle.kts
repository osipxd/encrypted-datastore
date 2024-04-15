import com.redmadrobot.build.dsl.kotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import internal.java

kotlinCompile {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_11
}
