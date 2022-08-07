package oger.util.java

import edu.sc.seis.launch4j.Launch4jPluginExtension
import org.gradle.api.Project

object ConfigureL4j {

    fun apply(project: Project) {
        val launch4j = project.extensions.getByType(Launch4jPluginExtension::class.java)
        launch4j.apply {
            downloadUrl = "https://adoptium.net/"
            libraryDir = "../lib"
        }
    }
}