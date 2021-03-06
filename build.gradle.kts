import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val kotlinVersion = "1.3.70"
val serializationVersion = "0.20.0"
val ktorVersion = "1.3.2"

plugins {
    kotlin("multiplatform") version "1.3.70"
    application //to run JVM part
    kotlin("plugin.serialization") version "1.3.70"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/") // react, styled, ...
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    jvm {
        withJava()
    }
    js {
        browser { }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation(kotlin("stdlib", kotlinVersion)) // or "stdlib-jdk8"
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion") // JVM dependency
                implementation("io.ktor:ktor-websockets:$ktorVersion")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serializationVersion")
                //todo: bugfix in kx.serialization?
                implementation(npm("text-encoding"))
                implementation(npm("abort-controller"))

                implementation("io.ktor:ktor-client-js:$ktorVersion") //include http&websockets
                //todo: bugfix in ktor-client?
                implementation(npm("bufferutil")) //TODO: Uncomment this and stuff breaks. WHY?
                implementation(npm("utf-8-validate"))

                //ktor client js json
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")
                implementation(npm("fs"))

                //React, React DOM + Wrappers (chapter 3)
                implementation("org.jetbrains:kotlin-react:16.13.0-pre.93-kotlin-1.3.70")
                implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.93-kotlin-1.3.70")
                implementation(npm("react", "16.13.0"))
                implementation(npm("react-dom", "16.13.0"))

                //Kotlin Styled (chapter 3)
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.93-kotlin-1.3.70")
                implementation(npm("styled-components"))
                implementation(npm("inline-style-prefixer"))
            }
        }
    }
}

application {
    mainClassName = "ServerKt"
}

//tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
//    outputFileName = "output.js"
//}

tasks.getByName<Jar>("jvmJar") {
    val taskName = "jsBrowserDevelopmentWebpack"
    dependsOn(tasks.getByName(taskName))
    val jsBrowserProductionWebpack = tasks.getByName<KotlinWebpack>(taskName)
    from(File(jsBrowserProductionWebpack.destinationDirectory, jsBrowserProductionWebpack.outputFileName))
}

tasks.getByName<JavaExec>("run") {
    dependsOn(tasks.getByName<Jar>("jvmJar"))
    classpath(tasks.getByName<Jar>("jvmJar"))
}