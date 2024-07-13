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
    compileOnly("org.gradlex:extra-java-module-info:1.8")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    val groovyMajor = 3
    testImplementation("org.codehaus.groovy:groovy-all:${groovyMajor}.0.21") {
        because("java-gradle-plugin requires 3.x")
    }
    testImplementation("org.spockframework:spock-core:2.3-groovy-${groovyMajor}.0")

    testImplementation("cglib:cglib-nodep:3.3.0")

    val jfxVer = "21.0.3"
    val os = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
    val jfxImpl = if (os.isLinux) ":linux"
    else if (os.isWindows) ":win"
    else if (os.isMacOsX) ":mac"
    else ""
    testImplementation("org.openjfx:javafx-base:${jfxVer}${jfxImpl}")
    testImplementation("org.openjfx:javafx-controls:${jfxVer}${jfxImpl}")
    testImplementation("org.openjfx:javafx-graphics:${jfxVer}${jfxImpl}")
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
