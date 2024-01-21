package oger.util.java

import edu.sc.seis.launch4j.Launch4jPluginExtension
import org.gradle.api.Project

object ConfigureL4j {
    internal var configured = false
        private set

    fun apply(project: Project) {
        configured = true
        val launch4j = project.extensions.getByType(Launch4jPluginExtension::class.java)
        launch4j.apply {
            downloadUrl.set("https://adoptium.net/")
            libraryDir.set("../lib")
            jvmOptions.addAll(
                listOf(
                    "-Dfile.encoding=UTF-8",
                    "-Dold.user.dir=%OLDPWD%"
                )
            )
            val gdrive = project.extensions.getByType(GDriveExtension::class.java)
            if (gdrive.mainClass.isPresent) mainClassName.set(gdrive.mainClass.get())

            project.plugins.findPlugin("org.openjfx.javafxplugin")?.let {
                jvmOptions.addAll(ConfigureJfx.getJvmOptions(libraryDir))
            }
        }
    }
}