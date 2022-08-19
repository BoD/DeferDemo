import com.github.benmanes.gradle.versions.updates.*

plugins {
    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
}

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        setOf("alpha", "beta", "rc").any { candidate.version.contains(it, ignoreCase = true) }
    }
}
