package oger.util.java

import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.internal.extensions.stdlib.toDefaultLowerCase
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object GDriveLocator {
    var DEBUG = false

    fun locate(): Path? {

        val letterDriveCandidates = File.listRoots()
            .map { it.toPath() }
            .map { it.resolve("My Drive") }
            .toCollection(ArrayList())

        addIfExists(letterDriveCandidates, "GDRIVE")

        letterDriveCandidates.add(Paths.get("${System.getenv("userprofile")}/Google Drive"))
        letterDriveCandidates.add(Paths.get("${System.getenv("userprofile")}/My Drive"))

        if (DEBUG) letterDriveCandidates.forEach { println("checking location $it: ${Files.exists(it)}") }

        return letterDriveCandidates.find { Files.exists(it) }
    }

    private fun addIfExists(candidates: MutableList<Path>, value: String) {
        System.getenv(value)?.let {
            candidates.add(0, Paths.get(it))
        }
        System.getProperty(value.toDefaultLowerCase().capitalized())?.let {
            candidates.add(0, Paths.get(it))
        }
    }

}