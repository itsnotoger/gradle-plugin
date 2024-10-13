package oger.util.java


import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification
import spock.lang.TempDir

class ToGDriveSpec extends Specification {

    @TempDir
    File testProjectDir
    File settingsFile
    File buildFile
    File outJar

    def setup() {
        settingsFile = new File(testProjectDir, 'settings.gradle')
        buildFile = new File(testProjectDir, 'build.gradle')

        // use this to test real path and remove environment below: def basePath = GDriveLocator.@INSTANCE.locate()
        def basePath = testProjectDir.toPath()
        outJar = basePath.resolve("syncme/gradle_jars/oger.test.jar").toFile()
        outJar.delete()
    }

    def cleanup() {
        outJar.delete()
    }

    def "actually copies files with toGDrive"() {
        given:
        settingsFile << "rootProject.name = 'oger.test'"
        buildFile << """
        plugins {
            id 'com.github.itsnotoger.gradle-plugin'
        }
        gdrive {
            debug = true
            type = oger.util.java.Type.JARLIBRARY
            GDriveJars = "/syncme/gradle_jars"
            GDriveApps = "/gradle_apps"
            mainClass = "oger.util.java.OgerPluginSpec"
        }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments('toGDrive')
                .withPluginClasspath()
                .withEnvironment(Map.of("GDRIVE", testProjectDir.toString()))
                .build()

        println getClass().getSimpleName() + " test build output { \n\t" + result.output.split("\n").join("\n\t") + "}"

        then:
        result.output.contains('toGDrive')
        result.task(":toGDrive").outcome == TaskOutcome.SUCCESS
        outJar.exists()
    }
}
