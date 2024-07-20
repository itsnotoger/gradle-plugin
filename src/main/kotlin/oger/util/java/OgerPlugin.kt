package oger.util.java

import oger.util.java.plugins.ConfigureExtraJavaModule
import oger.util.java.plugins.ConfigureJfx
import oger.util.java.plugins.ConfigureL4j
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion

class OgerPlugin : Plugin<Project> {

    private lateinit var gdrive: GDriveExtension
    private lateinit var gdriveRepo: MavenArtifactRepository
    private lateinit var fatJarRuntimeClasspath: NamedDomainObjectProvider<Configuration>

    override fun apply(project: Project) {
        applyPlugins(project)
        applyConfigurations(project)
        applyExtensions(project)
        applyTasks(project)
        applyRepositories(project)
        applyDependencies(project)
        applyPublishing(project)

        project.extensions.findByName("javafx")?.let { ConfigureJfx.apply(project) }
        project.extensions.findByName("launch4j")?.let { ConfigureL4j.apply(project) }

        ConfigureExtraJavaModule.disableTestClasspath(project)

        // without afterEvaluate, other plugins need to be defined BEFORE this plugin is defined
        project.afterEvaluate {
            afterEvaluate(project)
        }
    }

    private fun applyPlugins(project: Project) {
        project.plugins.apply {
            apply(JavaPlugin::class.java)
            apply(GroovyPlugin::class.java)
            apply(JavaLibraryPlugin::class.java)
            apply(MavenPublishPlugin::class.java)

//            apply(JavaFXPlugin::class.java)//TODO check back later if there is a way to apply third party plugins from a plugin
        }
    }

    private fun applyConfigurations(project: Project) {
        project.configurations.apply {
            val objects = project.objects

            fatJarRuntimeClasspath = register("fatJarRuntimeClasspath") {
                it.extendsFrom(named("runtimeClasspath").get())
                it.attributes { a ->
                    a.attribute(Attribute.of("javaModule", Boolean::class.javaObjectType), false)
                    a.attribute(
                        LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
                        objects.named(LibraryElements::class.java, LibraryElements.JAR)
                    )
                    a.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
                    a.attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling::class.java, Bundling.EXTERNAL))
                }
                it.isCanBeConsumed = false
            }
        }
    }

    private fun applyExtensions(project: Project) {
        project.extensions.apply {
            gdrive = create("gdrive", GDriveExtension::class.java)

            val java = getByType(JavaPluginExtension::class.java)
            java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    private fun applyExtensionsAfter(project: Project) {
        project.extensions.apply {
            val type = gdrive.type.get()

            if (type == Type.MAVENLIBRARY) {
                val publishing = getByType(PublishingExtension::class.java)
                publishing.publications.register("auto", MavenPublication::class.java) {
                    it.groupId = project.group.toString()
                    it.artifactId = project.name
                    it.version = project.version.toString()

                    it.from(project.components.getByName("java"))
                }
            }

            if (type.isLibrary()) {
                val java = getByType(JavaPluginExtension::class.java)
                java.withJavadocJar()
                java.withSourcesJar()
            }
        }
    }

    private fun applyTasks(project: Project) {
        project.tasks.apply {
            register("toGDrive", Copy::class.java) {
                it.group = "build"
                it.description = "Publish jar to Google Drive directory"

                it.dependsOn("build")

                @Suppress("DEPRECATION") // we want to support deprecated fields
                when (gdrive.type.get()) {
                    Type.INLINELIBRARY -> {
                        it.enabled = false
                    }

                    Type.LIBRARY,
                    Type.JARLIBRARY -> {
                        it.from(project.layout.buildDirectory.dir("libs"))
                        it.into(gdrive.fullJarPath.get())
                    }

                    Type.MAVENLIBRARY -> {
                        it.dependsOn("publishAutoPublicationToGDriveRepository")
                        // no copy necessary, publication task handles delivery
                        if (project.version.toString().endsWith("-SNAPSHOT")) {
                            println("WARNING: toGDrive is used with a snapshot version, creating duplicates")
                        }
                    }

                    Type.L4JAPPLICATION -> {
                        it.dependsOn("copyLib")
                        it.from(project.layout.buildDirectory.dir("launch4j"))
                        it.into(gdrive.fullAppPath.map { p -> p.resolve(project.name) })
                    }

                    Type.FATJARAPPLICATION -> {
                        it.dependsOn("fatJar")
                        it.from(project.layout.buildDirectory.dir("libs"))
                        it.into(gdrive.fullAppPath.map { p -> p.resolve(project.name) })
                    }

                    null -> throw IllegalArgumentException("gdrive.type must not be null")
                }
            }

            register("copyLib", Copy::class.java) {
                it.group = "other"
                it.description = "Copy build/lib into gDriveApps/lib"

                it.dependsOn("createAllExecutables")

                it.from(project.layout.buildDirectory.dir("lib"))
                it.into(gdrive.fullAppPath.map { p -> p.resolve("lib") })
            }

            register("fatJar", Jar::class.java) {
                it.group = "build"
                it.description = "creates a jar containing main classes, plus main resources, plus dependencies"

                // dependsOn build is not needed?

                it.manifest { m -> m.attributes(mapOf("Main-Class" to gdrive.mainClass)) }
                it.duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                val main = project.extensions.getByType(SourceSetContainer::class.java).getByName("main")

                it.from(main.output + main.resources)
                it.from({
                    fatJarRuntimeClasspath.get().map { cp -> if (cp.isDirectory) cp else project.zipTree(cp) }
                })

                it.exclude("module-info.class")
            }

            val compileJava = getByName("compileJava") as JavaCompile
            compileJava.options.encoding = "UTF-8"

            val compileTestJava = getByName("compileTestJava") as JavaCompile
            compileTestJava.options.encoding = "UTF-8"

            val test = getByName("test") as Test
            test.useJUnitPlatform()

            val javadoc = getByName("javadoc") as Javadoc
            javadoc.options.encoding = "UTF-8"
        }
    }

    private fun applyRepositories(project: Project) {
        project.repositories.apply {
            flatDir {
                it.dirs(gdrive.fullJarPath)
            }
            gdriveRepo = maven {
                it.setUrl(gdrive.fullJarUri)
                it.name = "GDrive"
            }
            maven {
                it.setUrl("https://jitpack.io")
                it.name = "JitPack"
            }
            mavenCentral()
        }
    }

    private fun applyDependencies(project: Project) {
        project.dependencies.apply {
            val groovyMajor = 3
            add("testImplementation", "org.codehaus.groovy:groovy-all:${groovyMajor}.0.21")
            add("testImplementation", "org.spockframework:spock-core:2.3-groovy-${groovyMajor}.0")
        }
    }

    private fun applyPublishing(project: Project) {
        project.extensions.getByType(PublishingExtension::class.java).apply {
            if (gdrive.type.get() == Type.MAVENLIBRARY) {
                repositories.add(gdriveRepo)
            }
        }
    }

    private fun afterEvaluate(project: Project) {
        val type = gdrive.type.get()

        project.plugins.findPlugin("org.openjfx.javafxplugin")?.let { ConfigureJfx.apply(project) }
        project.plugins.findPlugin("edu.sc.seis.launch4j")?.let { ConfigureL4j.apply(project) }
        project.plugins.findPlugin("org.gradlex.extra-java-module-info")
            ?.let { ConfigureExtraJavaModule.apply(project) }

        applyExtensionsAfter(project)

        if (type == Type.L4JAPPLICATION && !ConfigureL4j.configured) {
            throw IllegalArgumentException("you configured the type to ${Type.L4JAPPLICATION.name}, but the plugin 'edu.sc.seis.launch4j' is missing")
        }

        if (type.isApplication()) {
            if (!gdrive.mainClass.isPresent && (type == Type.FATJARAPPLICATION || project.tasks.findByName("createExe")?.enabled == true)) {
                println("WARNING: you set your type to application, but did not provide mainClass")
            }
        }
    }
}