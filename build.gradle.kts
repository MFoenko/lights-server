import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.gradle.node.npm.task.NpmTask

plugins {
	id("org.springframework.boot") version "3.0.2"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
	id("com.github.node-gradle.node") version "3.5.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

node {
	download.set(true)
	workDir.set(file("${project.buildDir}/nodejs"))
	npmWorkDir.set(file("${project.buildDir}/npm"))
}

tasks.register<NpmTask>("appNpmInstall") {
	description = "Installs all dependencies from package.json"
	workingDir.set(file("${project.projectDir}/src/main/webapp"))
	args.set(listOf("install"))
}

tasks.register<NpmTask>("appNpmBuild") {
	description = "Builds production version of the webapp"
	workingDir.set(file("${project.projectDir}/src/main/webapp"))
	args.set(listOf("run", "build"))
	dependsOn("appNpmInstall")
}

tasks.register<Copy>("copyWebApp") {
	from("src/main/webapp/build")
	into("build/resources/main/static/.")
	dependsOn("appNpmBuild")
}

tasks.get("compileJava").dependsOn("copyWebApp")