package oger.util.java

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.openjfx.gradle.JavaFXOptions

object ConfigureJfx {

    fun apply(project: Project) {
        val javafx = project.extensions.getByType(JavaFXOptions::class.java)

        javafx.apply {
            version = "21"
            modules("javafx.controls", "javafx.swing")
        }

        project.dependencies.apply {
            val jfxVersion = "21.0.1"
            add("implementation", "org.openjfx:javafx-graphics:$jfxVersion:win")
            add("implementation", "org.openjfx:javafx-controls:$jfxVersion:win")
        }
    }

    fun getJvmOptions(modulePath: Property<String>): List<String> {
        return listOf(
            "--module-path ${modulePath.get()}",
            "--add-modules=javafx.graphics,javafx.controls",
        )
    }
}