import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.70")
    }
}

plugins {
    kotlin("jvm").version("1.2.70")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(URI("https://packages.atlassian.com/maven-external/"))
}

tasks.withType(KotlinCompile::class).forEach {
    it.kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.getByName("test", Test::class).apply {
    filter {
        exclude("**/*IT.class")
    }
}

val zipWorkspace = task<Zip>("zipWorkspace") {
    from("$buildDir/jpt-workspace")
    archiveFileName.set("jpt-workspace.zip")
    destinationDirectory.set(buildDir)
    doLast {
        println("Zipped workspace at ${archiveFile.get()}")
    }
}

task<Test>("comparePerformance").apply {
    outputs.upToDateWhen { false }
    include("**/JiraPerformanceComparisonIT.class")
    val shadowJarTask = tasks.getByPath(":custom-vu:shadowJar")
    dependsOn(shadowJarTask)
    systemProperty("jpt.virtual-users.shadow-jar", shadowJarTask.outputs.files.files.first())
    maxHeapSize = "4g"
    finalizedBy(zipWorkspace)
}

dependencies {
    testCompile(project(":custom-vu"))
    testCompile("com.atlassian.performance.tools:jira-performance-tests:[3.3.0,4.0.0)")
    testCompile("com.atlassian.performance.tools:infrastructure:[4.12.0,5.0.0)")
    testCompile("com.atlassian.performance.tools:virtual-users:[3.10.0,4.0.0)")
    testCompile("com.atlassian.performance.tools:jira-software-actions:[1.3.2,2.0.0)")
    testCompile("com.atlassian.performance.tools:aws-infrastructure:[2.14.0,3.0.0)")
    testCompile("com.atlassian.performance.tools:aws-resources:[1.3.4,2.0.0)")
    testCompile("com.atlassian.performance.tools:report:[3.7.1,4.0.0)")
    testCompile("com.atlassian.performance.tools:concurrency:[1.0.0,2.0.0)")
    testCompile("junit:junit:4.12")
    testCompile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.70")
    val log4jVersion = "2.12.1"
    testCompile("org.apache.logging.log4j:log4j-api:$log4jVersion")
    testCompile("org.apache.logging.log4j:log4j-core:$log4jVersion")
    testRuntime("org.apache.logging.log4j:log4j-slf4j18-impl:$log4jVersion")
}

configurations.all {
    resolutionStrategy {
        failOnVersionConflict()
        activateDependencyLocking()
        resolutionStrategy {
            dependencySubstitution {
                substitute(module("org.apache.logging.log4j:log4j-slf4j-impl"))
                    .with(module("org.apache.logging.log4j:log4j-slf4j18-impl:2.12.1"))
            }
            eachDependency {
                when (requested.module.toString()) {
                    "org.slf4j:slf4j-api" -> useVersion("1.8.0-alpha2")
                    "commons-logging:commons-logging" -> useVersion("1.2")
                    "com.google.guava:guava" -> useVersion("23.6-jre")
                    "com.google.code.gson:gson" -> useVersion("2.8.2")
                    "org.jsoup:jsoup" -> useVersion("1.10.2")
                    "com.jcraft:jzlib" -> useVersion("1.1.3")
                    "com.fasterxml.jackson.core:jackson-core" -> useVersion("2.9.4")
                    "org.apache.httpcomponents:httpclient" -> useVersion("4.5.5")
                    "org.apache.commons:commons-csv" -> useVersion("1.4")
                    "org.apache.httpcomponents:httpcore" -> useVersion("4.4.9")
                    "org.codehaus.plexus:plexus-utils" -> useVersion("3.1.0")
                }
                when (requested.group) {
                    "org.jetbrains.kotlin" -> useVersion("1.2.70")
                    "org.apache.logging.log4j" -> useVersion("2.12.1")
                }
            }
        }
    }
}

tasks.wrapper {
    version = "5.1.1"
    distributionType = Wrapper.DistributionType.BIN
}
