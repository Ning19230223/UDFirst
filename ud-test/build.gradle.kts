val vertxVersion = "4.5.10"
val junitJupiterVersion = "5.9.1"

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    runtimeOnly("org.junit.platform:junit-platform-launcher")
    // rest-assured
    implementation("io.rest-assured:rest-assured:5.5.0")
    // 日志
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.8")
    // 内部模块依赖
    implementation(project(":ud-vertx"))
    implementation("io.vertx:vertx-core")
    // 统一vertx版本
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-web-client")
    // JUnit
    implementation("io.vertx:vertx-junit5")
    implementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}