package oger.util.java.plugins

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.openjfx.gradle.JavaFXOptions

object ConfigureJfx {

    val moduleList = listOf("javafx.base", "javafx.controls", "javafx.graphics", "javafx.swing")

    fun apply(project: Project) {
        val javafx = project.extensions.getByType(JavaFXOptions::class.java)

        javafx.apply {
            version = "21"
            modules = moduleList
        }

        project.dependencies.apply {
            val jfxVersion = "21.0.3"
            moduleList.forEach { module ->
                add("implementation", "org.openjfx:${module.replace('.', '-')}:$jfxVersion:win")
            }
        }
    }

    fun getJvmOptions(modulePath: Property<String>): List<String> {
        return listOf(
            "--module-path ${modulePath.get()}",
            "--add-modules=${moduleList.joinToString(",")}",
        )
    }
}