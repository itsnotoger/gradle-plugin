package oger.util.java

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.nio.file.Path
import java.nio.file.Paths

abstract class GDriveExtension {
    abstract val driveFolder: Property<String>
    abstract val gDriveJars: Property<String>
    internal val fullPath: Provider<Path>

    init {
        driveFolder.convention("${System.getenv("userprofile")}/Google Drive")
        fullPath = driveFolder.zip(gDriveJars) { a: String, b: String -> Paths.get(a, b) }
    }
}