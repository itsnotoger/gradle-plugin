package oger.util.java

import oger.util.java.GDriveLocator.locate
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.nio.file.Path
import java.nio.file.Paths

abstract class GDriveExtension {
    abstract val driveFolder: Property<String>
    abstract val gDriveJars: Property<String>
    internal val fullPath: Provider<Path>

    init {
        locate()?.let {
            driveFolder.convention(it.toString())
        }
        fullPath = driveFolder.zip(gDriveJars) { a: String, b: String -> Paths.get(a, b) }
    }
}