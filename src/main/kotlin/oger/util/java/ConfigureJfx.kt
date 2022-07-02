package oger.util.java

import org.gradle.api.Project
import org.openjfx.gradle.JavaFXOptions

object ConfigureJfx {

    fun apply(project: Project) {
        val javafx = project.extensions.getByName("javafx") as JavaFXOptions

        javafx.apply {
            version = "17"
            modules("javafx.controls", "javafx.swing")
        }
    }
}