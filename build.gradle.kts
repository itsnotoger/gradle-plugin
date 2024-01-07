plugins {
    groovy
    kotlin("jvm") version "1.9.20"
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.github.itsnotoger"
version = "0.0.1-SNAPSHOT"
val githubProjectName = "gradle-plugin"

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

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
    compileOnly("org.openjfx:javafx-plugin:0.1.0")
    compileOnly("edu.sc.seis.launch4j:edu.sc.seis.launch4j.gradle.plugin:3.0.5")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.17") {
        because("java-gradle-plugin requires 3.x")
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
