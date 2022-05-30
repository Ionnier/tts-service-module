val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val rabbitmq: String by project
val gson_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.21"
}

group = "org.example"
version = "0.0.1"
application {
    mainClass.set("org.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.rabbitmq:amqp-client:$rabbitmq")
    implementation ("com.google.code.gson:gson:$gson_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}