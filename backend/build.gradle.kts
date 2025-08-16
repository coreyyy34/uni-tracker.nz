import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.0" apply false
    kotlin("plugin.spring") version "2.2.0" apply false
    id("org.springframework.boot") version "3.5.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
    group = "nz.unitracker"
    repositories { mavenCentral() }

    apply(plugin = "org.jetbrains.kotlin.jvm")

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
        "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
    }

    // common tests configuration
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

// spring boot specific configuration - allows additional subprojects in the future that aren't using spring boot
configure(subprojects.filter { it.name in listOf("api", "auth") }) {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    dependencies {
        // spring boot starters
        "implementation"("org.springframework.boot:spring-boot-starter")
        "implementation"("org.springframework.boot:spring-boot-starter-web")
        "implementation"("org.springframework.boot:spring-boot-starter-actuator")

        // kotlin
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")

        // testing
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }
}