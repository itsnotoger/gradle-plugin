plugins {
    groovy
    kotlin("jvm") version "1.7.10"
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.github.itsnotoger"
version = "0.0.1-SNAPSHOT"
val githubProjectName = "gradle-plugin"

gradlePlugin {
    plugins {
        register("OgerPlugin") {
            id = "$group.$githubProjectName"
            implementationClass = "oger.util.java.OgerPlugin"
        }
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

tasks {
    test {
        useJUnitPlatform()
    }
}

dependencies {
    compileOnly("org.openjfx:javafx-plugin:0.0.13")
    compileOnly("edu.sc.seis.launch4j:edu.sc.seis.launch4j.gradle.plugin:2.5.3")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.9") {
        because("java-gradle-plugin requires 3.0.9")
    }
    testImplementation("org.spockframework:spock-core:2.3-groovy-3.0")
    testImplementation("cglib:cglib-nodep:3.3.0")
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            groupId = "$group"
            artifactId = githubProjectName
            version = "${project.version}"

            from(components["java"])
        }
    }
}
