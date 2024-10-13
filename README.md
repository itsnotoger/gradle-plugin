[![](https://jitpack.io/v/itsnotoger/gradle-plugin.svg)](https://jitpack.io/#itsnotoger/gradle-plugin)

# gradle-plugin

My Gradle plugin for Java projects

It is written in Kotlin, but configures Java projects.

My basic setup is Spock (Groovy) for unit tests, and maven builds are published on jitpack.

## Should you use this?

Probably not, as this was made to propel my own legacy projects into the Gradle world.

The primary functionality is to establish a **maven repository on my Google Drive**.
It locates your Drive directory dynamically.
I can publish my libraries to it, and depend on them on any of my machines.
This is still nice to have, as it allows me to build my own projects anywhere, without having to make them public.
For instance, Jitpack also supports private repositories, but only in paid plans.
For more details on how this is done, see [my answer on StackOverflow](https://stackoverflow.com/a/74093635).
If you are able to have all your projects public, you do not need this plugin functionality.

Secondly, it supports some **application publication modes** that I like.
You can either publish Windows executable files (powered by the launch4j gradle plugin) or a fatjar.
For these, JavaFx support is added as well (namely: pulling the dependencies via JavaFx gradle plugin, and adding the
necessary launch options).
Overall, if you wanted to create a JavaFx executable, this makes the gradle code required to do so pretty small.

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
    id("com.github.itsnotoger.gradle-plugin") version "XXX" // see jitpack badge at the top of readme for latest version
}
```

Supported Gradle versions >= `8.9` (adjust in `gradle-wrapper.properties`).

It should locate your Google Drive installation automatically, but you can specify a system prop `Gdrive` or env
variable `GDRIVE` if needed.
