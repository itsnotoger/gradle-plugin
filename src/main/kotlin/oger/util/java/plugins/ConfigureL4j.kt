package oger.util.java.plugins

import edu.sc.seis.launch4j.Launch4jPluginExtension
import oger.util.java.GDriveExtension
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
                "-Dfile.encoding=UTF-8",
                """-Dold.user.dir="%OLDPWD%""""
            )
            val gdrive = project.extensions.getByType(GDriveExtension::class.java)

            mainClassName.convention(gdrive.mainClass)

            project.plugins.findPlugin("org.openjfx.javafxplugin")?.let {
                jvmOptions.addAll(ConfigureJfx.getJvmOptions(libraryDir))
            }
        }
    }
}