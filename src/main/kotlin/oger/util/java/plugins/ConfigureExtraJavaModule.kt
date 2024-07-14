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

            module("com.github.Kaned1as:jaudiotagger", "org.jaudiotagger") {
                it.requires("okio")
                it.exports("org.jaudiotagger")
                it.exports("org.jaudiotagger.audio")
                it.exports("org.jaudiotagger.audio.aiff")
                it.exports("org.jaudiotagger.audio.aiff.chunk")
                it.exports("org.jaudiotagger.audio.asf")
                it.exports("org.jaudiotagger.audio.asf.data")
                it.exports("org.jaudiotagger.audio.asf.io")
                it.exports("org.jaudiotagger.audio.asf.util")
                it.exports("org.jaudiotagger.audio.dsf")
                it.exports("org.jaudiotagger.audio.exceptions")
                it.exports("org.jaudiotagger.audio.flac")
                it.exports("org.jaudiotagger.audio.flac.metadatablock")
                it.exports("org.jaudiotagger.audio.generic")
                it.exports("org.jaudiotagger.audio.iff")
                it.exports("org.jaudiotagger.audio.io")
                it.exports("org.jaudiotagger.audio.mp3")
                it.exports("org.jaudiotagger.audio.mp4")
                it.exports("org.jaudiotagger.audio.ogg")
                it.exports("org.jaudiotagger.audio.ogg.util")
                it.exports("org.jaudiotagger.audio.opus")
                it.exports("org.jaudiotagger.audio.opus.util")
                it.exports("org.jaudiotagger.audio.real")
                it.exports("org.jaudiotagger.audio.wav")
                it.exports("org.jaudiotagger.audio.wav.chunk")
                it.exports("org.jaudiotagger.logging")
                it.exports("org.jaudiotagger.tag")
                it.exports("org.jaudiotagger.tag.aiff")
                it.exports("org.jaudiotagger.tag.asf")
                it.exports("org.jaudiotagger.tag.datatype")
                it.exports("org.jaudiotagger.tag.flac")
                it.exports("org.jaudiotagger.tag.id3")
                it.exports("org.jaudiotagger.tag.id3.framebody")
                it.exports("org.jaudiotagger.tag.id3.reference")
                it.exports("org.jaudiotagger.tag.id3.valuepair")
                it.exports("org.jaudiotagger.tag.images")
                it.exports("org.jaudiotagger.tag.lyrics3")
                it.exports("org.jaudiotagger.tag.mp4")
                it.exports("org.jaudiotagger.tag.mp4.field")
                it.exports("org.jaudiotagger.tag.options")
                it.exports("org.jaudiotagger.tag.reference")
                it.exports("org.jaudiotagger.tag.vorbiscomment")
                it.exports("org.jaudiotagger.tag.wav")
                it.exports("org.jaudiotagger.utils")
                it.exports("org.jaudiotagger.utils.tree")
                it.exports("org.jcodec.containers.mp4")
                it.exports("org.jcodec.containers.mp4.boxes")
            }
            module("com.squareup.okio:okio-jvm", "okio") {
                it.requires("kotlin.stdlib")
                it.exports("okio")
            }
            knownModule("org.jetbrains.kotlin:kotlin-stdlib", "kotlin.stdlib")
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