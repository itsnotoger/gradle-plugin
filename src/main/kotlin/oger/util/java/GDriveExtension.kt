@file:Suppress("LeakingThis")

package oger.util.java

import oger.util.java.GDriveLocator.locate
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.nio.file.Path
import java.nio.file.Paths

abstract class GDriveExtension {
    abstract val driveFolder: Property<String>
    abstract val gDriveJars: Property<String>
    internal val fullJarPath: Provider<Path>

    abstract val type: Property<Type>
    abstract val gDriveApps: Property<String>
    internal val fullAppPath: Provider<Path>

    abstract val mainClass: Property<String>

    init {
        locate()?.let {
            driveFolder.convention(it.toString())
        }
        fullJarPath = driveFolder.zip(gDriveJars) { a: String, b: String -> Paths.get(a, b) }

        type.convention(Type.LIBRARY)

        fullAppPath = driveFolder.zip(gDriveApps.orElse("/gradle_apps")) { a: String, b: String -> Paths.get(a, b) }
    }
}

enum class Type {
    LIBRARY, L4JAPPLICATION, FATJARAPPLICATION
}