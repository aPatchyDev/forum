import net.ltgt.gradle.errorprone.*

plugins {
	java
	id("net.ltgt.errorprone") version "4.3.0"
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "io.github.apatchydev"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jspecify:jspecify:1.0.0")
	errorprone("com.google.errorprone:error_prone_core:2.41.0")
	errorprone("com.uber.nullaway:nullaway:0.12.7")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<JavaCompile> {
	options.errorprone {
		check("NullAway", CheckSeverity.ERROR)
		option("NullAway:OnlyNullMarked", true)
	}

	// Disable NullAway when running Gradle Task with name containing "test"
	if (name.lowercase().contains("test")) {
		options.errorprone {
			disable("NullAway")
		}
	}
}