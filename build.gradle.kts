import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.0"
    `maven-publish`
    idea
    id("com.github.johnrengelman.shadow") version ("6.0.0")
    kotlin("kapt") version "1.4.0"
}

group = "com.xsc"
version = "serverless"

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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.0")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:3.1.1")
    implementation("com.amazonaws:aws-lambda-java-log4j2:1.2.0")
    implementation(platform("software.amazon.awssdk:bom:2.15.2"))
    implementation("software.amazon.awssdk:dynamodb") {
        exclude("software.amazon.awssdk", "netty-nio-client")
        exclude("software.amazon.awssdk", "apache-client")
    }
    implementation("software.amazon.awssdk:dynamodb-enhanced") {
        exclude("software.amazon.awssdk", "netty-nio-client")
        exclude("software.amazon.awssdk", "apache-client")
    }
    implementation("software.amazon.awssdk:url-connection-client")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("com.google.code.gson:gson:2.8.6")
    testImplementation(kotlin("test-junit"))
    implementation("com.google.dagger:dagger:2.28.3")
    kapt("com.google.dagger:dagger-compiler:2.28.3")
}

tasks.build {
    finalizedBy(getTasksByName("shadowJar", false))
}

task<Exec>("deploy") {
    dependsOn("build")
    commandLine("serverless", "deploy")
}