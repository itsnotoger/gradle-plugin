package oger.util.java

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class OgerPluginSpec extends Specification {
    def "creates an extension named gdrive"() {
        given:
        Project project = ProjectBuilder.builder().withName("testproject").build()

        when:
        project.pluginManager.apply(OgerPlugin)

        then:
        def gdrive = project.extensions.getByName("gdrive") as GDriveExtension
        gdrive.driveFolder.get() != null
    }
}
