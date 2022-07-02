plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.7.0"
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
}

dependencies {
    compileOnly("org.openjfx:javafx-plugin:0.0.13")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
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
val driveFolder = "${System.getenv("userprofile")}/Google Drive$gDriveJars"

//repositories {
//    flatDir {
//        dirs(driveFolder)
//    }
//    mavenCentral()
//}
//
//
//dependencies {
//    // https://mvnrepository.com/
//    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
//    testImplementation("org.codehaus.groovy:groovy-all:3.0.11")
//}











