package oger.util.java.plugins

import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.plugins.JavaPlugin
import org.gradlex.javamodule.moduleinfo.ExtraJavaModuleInfoPluginExtension

object ConfigureExtraJavaModule {

    fun apply(project: Project) {
        val extraJavaModuleInfo = project.extensions.getByType(ExtraJavaModuleInfoPluginExtension::class.java)
        extraJavaModuleInfo.apply {
            module("com.google.android.tools:dx", "com.android.dx") {
                it.exports("com.android.dx")
                it.exports("com.android.dx.cf.attrib")
                it.exports("com.android.dx.cf.code")
                it.exports("com.android.dx.cf.cst")
                it.exports("com.android.dx.cf.direct")
                it.exports("com.android.dx.cf.iface")
                it.exports("com.android.dx.command")
                it.exports("com.android.dx.command.annotool")
                it.exports("com.android.dx.command.dexer")
                it.exports("com.android.dx.command.dump")
                it.exports("com.android.dx.command.findusages")
                it.exports("com.android.dx.command.grep")
                it.exports("com.android.dx.dex")
                it.exports("com.android.dx.dex.cf")
                it.exports("com.android.dx.dex.code")
                it.exports("com.android.dx.dex.code.form")
                it.exports("com.android.dx.dex.file")
                it.exports("com.android.dx.gen")
                it.exports("com.android.dx.io")
                it.exports("com.android.dx.io.instructions")
                it.exports("com.android.dx.merge")
                it.exports("com.android.dx.rop.annotation")
                it.exports("com.android.dx.rop.code")
                it.exports("com.android.dx.rop.cst")
                it.exports("com.android.dx.rop.type")
                it.exports("com.android.dx.ssa")
                it.exports("com.android.dx.ssa.back")
                it.exports("com.android.dx.util")
            }

            module("com.nativelibs4java:bridj", "org.bridj") {
                it.exports("org.bridj")
                it.exports("org.bridj.cpp.com")
                it.exports("org.bridj.cpp.com.shell")
                it.requires("com.android.dx")
            }

            module("org.eclipse.fx:org.eclipse.fx.drift", "org.eclipse.fx.drift") {
                it.exports("org.eclipse.fx.drift")
                it.exports("org.eclipse.fx.drift.impl")
                it.exports("org.eclipse.fx.drift.util")
                it.requires("javafx.graphics")
            }
        }
    }

    fun disableTestClasspath(project: Project) {
        project.plugins.withType(JavaPlugin::class.java) {
            listOf("testCompileClasspath", "testRuntimeClasspath").forEach { configName ->
                project.configurations.named(configName).configure {
                    it.attributes.attribute(Attribute.of("javaModule", Boolean::class.javaObjectType), false)
                }
            }
        }
    }
}