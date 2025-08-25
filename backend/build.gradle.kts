import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "2.2.0" apply false
    kotlin("plugin.spring") version "2.2.0" apply false
    id("org.springframework.boot") version "3.5.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("jacoco") apply true
    java

    // lint
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0" apply false
}

val exposedVersion = "1.0.0-beta-5"
val jacksonVersion = "2.19.2"

subprojects {
    group = "nz.unitracker"
    repositories { mavenCentral() }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "jacoco")
    apply(plugin = "java")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<JavaPluginExtension> {
        toolchain { languageVersion = JavaLanguageVersion.of(24) }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_24)
        }
    }

    // common dependencies for all subprojects
    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")
        "implementation"("io.github.oshai:kotlin-logging-jvm:7.0.13")
        "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
    }

    configure<KtlintExtension> {
        version.set("1.7.1")
        android.set(false)
        ignoreFailures.set(false) // fail build on violations
        outputToConsole.set(true)

        reporters {
            reporter(ReporterType.HTML)
        }

        filter {
            include("**/kotlin/**")
        }
    }

    // common test configuration
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    tasks.named<Test>("test") {
        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.withType<JacocoReport>().configureEach {
        dependsOn(tasks.named<Test>("test"))
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
        }
    }
}

// spring Boot specific configuration
configure(subprojects.filter { it.name in listOf("api", "auth") }) {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    dependencies {
        "implementation"("org.springframework.boot:spring-boot-starter")
        "implementation"("org.springframework.boot:spring-boot-starter-web")
        "implementation"("org.springframework.boot:spring-boot-starter-actuator")

        // database
        implementation("org.jetbrains.exposed:exposed-spring-boot-starter:$exposedVersion")
        implementation("org.jetbrains.exposed:spring-transaction:$exposedVersion")
        implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
        implementation("com.h2database:h2")

        // jackson
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
        implementation("com.fasterxml.jackson.module:jackson-module-afterburner:$jacksonVersion")

        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    sourceSets {
        val main by getting

        val testIntegration by creating {
            compileClasspath += main.output
            runtimeClasspath += output + compileClasspath
        }
    }

    configurations {
        named("testIntegrationImplementation") {
            extendsFrom(configurations["implementation"], configurations["testImplementation"])
        }
        named("testIntegrationRuntimeOnly") {
            extendsFrom(configurations["runtimeOnly"], configurations["testRuntimeOnly"])
        }
    }

    tasks.register<Test>("testIntegration") {
        description = "Runs integration tests."
        group = "verification"
        testClassesDirs = sourceSets["testIntegration"].output.classesDirs
        classpath = sourceSets["testIntegration"].runtimeClasspath

        useJUnitPlatform {}
    }

    tasks.register<JacocoReport>("jacocoTestIntegrationReport") {
        dependsOn(tasks.named("testIntegration"))
        group = "verification"
        description = "Generates Jacoco coverage report for integration tests"

        executionData.setFrom(
            fileTree(layout.buildDirectory.dir("jacoco")) {
                include("testIntegration.exec")
            },
        )

        sourceDirectories.setFrom(sourceSets["main"].allSource.srcDirs)
        classDirectories.setFrom(sourceSets["main"].output)

        reports {
            xml.required.set(true)
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacocoIntegration"))
            csv.required.set(false)
        }
    }

    tasks.named("check") {
        dependsOn("testIntegration", "jacocoTestIntegrationReport")
    }
}
