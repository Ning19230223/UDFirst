import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val vertxVersion = "4.5.10"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "com.bsy.udvertx.Verticle1"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
    mainClass.set(launcherClassName)
}

dependencies {
    implementation("io.vertx:vertx-core")
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))  // 确保所有Vertx依赖有相同的版本号
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-web-client")
    // JUnit5
    implementation("io.vertx:vertx-junit5")
    implementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    // RestAssured
    implementation("io.rest-assured:rest-assured:5.5.0")
    // 这个两个依赖是输出日志
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.8")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
}






