package oger.util.java

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

class OgerPlugin : Plugin<Project> {

    private lateinit var gdrive: GDriveExtension

    override fun apply(project: Project) {
        applyPlugins(project)
        applyExtensions(project)
        applyTasks(project)
        applyRepositories(project)
        applyDependencies(project)

        project.extensions.findByName("javafx")?.let { ConfigureJfx.apply(project) }
        project.extensions.findByName("launch4j")?.let { ConfigureL4j.apply(project) }

        // without afterEvaluate, other plugins need to be defined BEFORE this plugin is defined
        project.afterEvaluate {
            val type = gdrive.type.get()

            project.plugins.findPlugin("org.openjfx.javafxplugin")?.let { ConfigureJfx.apply(project) }

            if (project.plugins.findPlugin("edu.sc.seis.launch4j") != null) {
                ConfigureL4j.apply(project)

                project.tasks.register("copyLib", Copy::class.java) {
                    it.group = "launch4j"
                    it.description = "Copy build/lib into gDriveApps/lib"

                    it.dependsOn("createAllExecutables")

                    it.from("${project.buildDir}/lib")
                    it.into(gdrive.fullAppPath.get().resolve("lib"))
                }
            } else if (type == Type.L4JAPPLICATION) {
                throw IllegalArgumentException("you configured the type to L4JAPPLICATION, but the plugin 'edu.sc.seis.launch4j' is missing")
            }

            if (type == Type.L4JAPPLICATION || type == Type.FATJARAPPLICATION) {
                if (!gdrive.mainClass.isPresent && (type == Type.FATJARAPPLICATION || project.tasks.getByName("createExe").enabled)) {
                    println("WARNING: you set your type to application, but did not provide mainClass")
                }
            }
        }
    }

    private fun applyPlugins(project: Project) {
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(GroovyPlugin::class.java)
        project.plugins.apply(JavaLibraryPlugin::class.java)
        project.plugins.apply(MavenPublishPlugin::class.java)

        //TODO check back later if there is a way to apply third party plugins from a plugin
//        project.plugins.apply(JavaFXPlugin::class.java)
    }

    private fun applyExtensions(project: Project) {
        gdrive = project.extensions.create("gdrive", GDriveExtension::class.java)
    }

    private fun applyTasks(project: Project) {
        project.tasks.apply {
            register("toGDrive", Copy::class.java) {
                it.group = "build"
                it.description = "Publish jar to Google Drive directory"

                it.dependsOn("build")

                when (gdrive.type.get()) {
                    Type.LIBRARY -> {
                        it.from("${project.buildDir}/libs")
                        it.into(gdrive.fullJarPath.get())
                    }
                    Type.L4JAPPLICATION -> {
                        it.dependsOn("copyLib")
                        it.from("${project.buildDir}/launch4j")
                        it.into(gdrive.fullAppPath.get().resolve(project.name))
                    }
                    Type.FATJARAPPLICATION -> {
                        it.dependsOn("fatJar")
                        it.from("${project.buildDir}/libs")
                        it.into(gdrive.fullAppPath.get().resolve(project.name))
                    }
                    else -> throw IllegalArgumentException(gdrive.type.get().toString())
                }
            }

            register("fatJar", Jar::class.java) {
                it.group = "build"
                it.description = "creates a jar containing main classes, plus main resources, plus dependencies"

                it.dependsOn("build")

                it.manifest { m -> m.attributes(mapOf("Main-Class" to gdrive.mainClass)) }
                it.duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                val main = project.extensions.getByType(SourceSetContainer::class.java).getByName("main")
                val deps =
                    project.configurations.getByName("runtimeClasspath").map { cpe -> if (cpe.isDirectory) cpe else project.zipTree(cpe) }
                it.from(deps + main.output + main.resources)
            }

            val javaCompile = getByName("compileJava") as JavaCompile
            javaCompile.options.encoding = "UTF-8"
            val compileTestJava = getByName("compileTestJava") as JavaCompile
            compileTestJava.options.encoding = "UTF-8"
            val test = getByName("test") as Test
            test.useJUnitPlatform()
        }
    }

    private fun applyRepositories(project: Project) {
        project.repositories.apply {
            flatDir {
                it.dirs(gdrive.fullJarPath)
            }
            mavenCentral()
        }
    }

    private fun applyDependencies(project: Project) {
        project.dependencies.apply {
            add("testImplementation", "org.spockframework:spock-core:2.1-groovy-3.0")
            add("testImplementation", "org.codehaus.groovy:groovy-all:3.0.11")
        }
    }
}