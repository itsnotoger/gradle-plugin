[![](https://jitpack.io/v/itsnotoger/gradle-plugin.svg)](https://jitpack.io/#itsnotoger/gradle-plugin)

# gradle-plugin
My Gradle plugin for Java projects

It is written in Kotlin, but configures Java projects.

My basic setup is Spock (Groovy) for unit tests, and maven builds are published on jitpack.

## Quick use guide for Gradle

```kotlin
// settings.gradle.kts
pluginManagement {
    resolutionStrategy.eachPlugin {
        requested.apply {
            if ("$id".startsWith("com.github.")) {
                val (_, _, user, name) = "$id".split(".", limit = 4)
                useModule("com.github.$user:$name:$version")
            }
        }
    }

    repositories {
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
}
```

```kotlin
// build.gradle.kts
plugins {
    id("com.github.itsnotoger.gradle-plugin") version "a495bf4e7d" // git commit id, or tag
}
```
