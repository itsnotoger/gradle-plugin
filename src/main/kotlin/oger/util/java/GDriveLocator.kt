package oger.util.java

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object GDriveLocator {
    fun locate(): Path? {
        val letterDriveCandidates = File.listRoots()
            .map { it.toPath() }
            .map { it.resolve("My Drive") }
            .toCollection(ArrayList())

        letterDriveCandidates.add(Paths.get("${System.getenv("userprofile")}/Google Drive"))

        return letterDriveCandidates.find { Files.exists(it) }
    }
}