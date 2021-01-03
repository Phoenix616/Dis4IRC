import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.ByteArrayOutputStream
import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"

    id("net.minecrell.licenser") version "0.4.1"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

group = "io.zachbr"
version = "1.2.1-SNAPSHOT"

repositories {
    maven("https://dl.bintray.com/dv8fromtheworld/maven/")
    maven("https://repo.spongepowered.org/maven")
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.kitteh.irc:client-lib:7.3.0")
    implementation("club.minnced:discord-webhooks:0.5.0")
    implementation("net.dv8tion:JDA:4.2.0_222") {
        exclude(module = "opus-java")
    }

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.json:json:20180813")
    implementation("org.spongepowered:configurate-hocon:3.7.1")
    implementation("com.atlassian.commonmark:commonmark:0.15.2")
    implementation("com.atlassian.commonmark:commonmark-ext-gfm-strikethrough:0.15.2")

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.apache.logging.log4j:log4j-core:2.13.2")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.13.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.4.21")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    withType<ShadowJar> {
        manifest {
            attributes["Main-Class"] = "io.zachbr.dis4irc.Dis4IRCKt"
        }

        from(file("LICENSE.md"))
        archiveClassifier.set("")
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

    processResources {
        expand(
            "projectName" to rootProject.name,
            "projectVersion" to version,
            "projectGitHash" to getGitHash(),
            "projectSourceRepo" to "https://github.com/zachbr/Dis4IRC"
        )
    }

    test {
        testLogging.showStandardStreams = true
        useJUnitPlatform()
    }
}

// updateLicenses | checkLicenses
license {
    header = project.file("HEADER.txt")
    ext {
        set("name", "Dis4IRC")
        set("year", "2018-2021")
    }
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream() // cannot be fully qualified, ¯\_(ツ)_/¯
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

dependencyManagement {
    imports {
        mavenBom("org.apache.logging.log4j:log4j-bom:2.13.2")
    }
}
