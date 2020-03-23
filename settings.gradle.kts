pluginManagement {
    repositories {
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }

        mavenCentral()

        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
}
rootProject.name = "vollstapel"

