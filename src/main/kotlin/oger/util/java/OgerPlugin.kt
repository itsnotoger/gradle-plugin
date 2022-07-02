package oger.util.java

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

class OgerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // plugins
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(GroovyPlugin::class.java)
        project.plugins.apply(JavaLibraryPlugin::class.java)

        //TODO check back later if there is a way to apply third party plugins from a plugin
//        project.plugins.apply(JavaFXPlugin::class.java)

        // tasks
        val gdrive = project.extensions.create("gdrive", GDriveExtension::class.java)

        project.tasks.register("toGDrive", Copy::class.java) {
            it.dependsOn("build")

            it.from("${project.buildDir}/libs")
            it.into(gdrive.fullPath.get())

            it.group = "build"
            it.description = "Publish jar to Google Drive directory"
        }

        val javaCompile = project.tasks.getByName("compileJava") as JavaCompile
        javaCompile.options.encoding = "UTF-8"
        val compileTestJava = project.tasks.getByName("compileTestJava") as JavaCompile
        compileTestJava.options.encoding = "UTF-8"
        val test = project.tasks.getByName("test") as Test
        test.useJUnitPlatform()

        // repositories
        project.repositories.apply {
            flatDir {
                it.dirs(gdrive.fullPath)
            }
            mavenCentral()
        }

        project.extensions.findByName("javafx")?.let { ConfigureJfx.apply(project) }

        // dependencies
        project.dependencies.apply {
            add("testImplementation", "org.spockframework:spock-core:2.1-groovy-3.0")
            add("testImplementation", "org.codehaus.groovy:groovy-all:3.0.11")
        }
    }
}