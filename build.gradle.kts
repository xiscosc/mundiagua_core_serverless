import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.3.72"
    `maven-publish`
    id("io.spring.dependency-management") version ("1.0.9.RELEASE")
    id("com.github.johnrengelman.shadow") version ("6.0.0")
}

group = "com.xsc"
version = "alpha"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    transform(Log4j2PluginsCacheFileTransformer::class.java)
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")
    implementation(platform("com.amazonaws:aws-java-sdk-bom:1.11.832"))
    implementation("com.amazonaws:aws-java-sdk-dynamodb")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:3.1.1")
    implementation("com.amazonaws:aws-lambda-java-log4j2:1.2.0")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("com.google.code.gson:gson:2.8.6")
    testImplementation(kotlin("test-junit"))
}

tasks.build {
    finalizedBy(getTasksByName("shadowJar", false))
}

task<Exec>("deploy") {
    dependsOn("build")
    commandLine("serverless", "deploy")
}