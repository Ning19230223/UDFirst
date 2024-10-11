plugins {
    id("java")
}

allprojects {
    apply(plugin = "java")

    group = "com.bsy"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    java {
        toolchain{
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    tasks.test {
        useJUnitPlatform()
    }

}