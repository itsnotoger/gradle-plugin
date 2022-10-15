plugins {
    groovy
    kotlin("jvm") version "1.7.10"
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        register("OgerPlugin") {
            id = "oger.util.java"
            implementationClass = "oger.util.java.OgerPlugin"
        }
    }
}

group = "oger.util.java"
version = "0.0.1-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

tasks {
    test {
        useJUnitPlatform()
    }

    register<Copy>("toGDrive") {
        dependsOn(build)

        from("${project.buildDir}/libs")
        into(driveFolder)

        group = "build"
        description = "Publish jar to Google Drive directory"
    }
}

val gDriveJars: String by project
apply("locate.gradle.kts")
val locate: () -> File? by ext
val driveFolder = "${locate()}/$gDriveJars"

dependencies {
    compileOnly("org.openjfx:javafx-plugin:0.0.13")
    compileOnly("edu.sc.seis.launch4j:edu.sc.seis.launch4j.gradle.plugin:2.5.3")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.9") {
        because("java-gradle-plugin requires 3.0.9")
    }
    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
    testImplementation("cglib:cglib-nodep:3.3.0")
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            groupId = group.toString()
            artifactId = project.name
            version = version.toString()

            from(components["java"])
        }
    }
}
