@file:Suppress("LeakingThis")

package oger.util.java

import oger.util.java.GDriveLocator.locate
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

abstract class GDriveExtension {
    abstract val driveFolder: Property<String>
    abstract val gDriveJars: Property<String>
    internal val fullJarPath: Provider<Path>
    internal val fullJarUri: Provider<URI>

    abstract val type: Property<Type>
    abstract val gDriveApps: Property<String>
    internal val fullAppPath: Provider<Path>

    abstract val mainClass: Property<String>

    init {
        locate()?.let {
            driveFolder.convention(it.toString())
        }
        fullJarPath = driveFolder.zip(gDriveJars) { a: String, b: String -> Paths.get(a, b) }
        fullJarUri = fullJarPath.map { it.toUri() }

        type.convention(Type.MAVENLIBRARY)

        fullAppPath = driveFolder.zip(gDriveApps.orElse("/gradle_apps")) { a: String, b: String -> Paths.get(a, b) }
    }
}

enum class Type {
    @Deprecated("renamed", ReplaceWith("JARLIBRARY"), DeprecationLevel.WARNING)
    LIBRARY,

    /** Library that does not get directly published. Rather, it is included in another publication, usually a sibling project. */
    INLINELIBRARY,

    /** Library that gets published as a plain jar */
    JARLIBRARY,

    /** Library that gets published as a full-fledged maven library */
    MAVENLIBRARY,

    /** Application that gets published as a Windows .exe */
    L4JAPPLICATION,

    /** Application that gets published as a runnable fatjar */
    FATJARAPPLICATION;

    @Suppress("DEPRECATION") // we want to support deprecated fields
    fun isLibrary(): Boolean = when (this) {
        INLINELIBRARY,
        LIBRARY,
        JARLIBRARY,
        MAVENLIBRARY -> true

        L4JAPPLICATION,
        FATJARAPPLICATION -> false
    }

    fun isApplication(): Boolean = !isLibrary()
}