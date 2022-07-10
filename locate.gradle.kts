import java.io.File

buildscript {
    /** Bootstrap version of GDriveLocator */
    val locate = fun (): File? {
        val letterDriveCandidates = File.listRoots()
            .map { it.toPath() }
            .map { it.resolve("My Drive") }
            .map { file(it.toString()) }
            .toCollection(ArrayList())

        letterDriveCandidates.add(file("${System.getenv("userprofile")}/Google Drive"))

        return letterDriveCandidates.find { it.exists() }
    }

    extra.set("locate", locate)
}