import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform") version "1.3.70-eap-184"
    application //to run JVM part
    kotlin("plugin.serialization") version "1.3.70-eap-184"
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
                implementation("io.ktor:ktor-serialization:1.3.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.14.0-1.3.70-eap-134") // JVM dependency
                implementation("io.ktor:ktor-client-core:1.3.0")
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
                implementation("io.ktor:ktor-server-core:1.3.0")
                implementation("io.ktor:ktor-server-netty:1.3.0")
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION)) // or "stdlib-jdk8"
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0-1.3.70-eap-134") // JVM dependency
                implementation("io.ktor:ktor-websockets:1.3.0")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.14.0-1.3.70-eap-134")
                //todo: bugfix in kx.serialization?
                implementation(npm("text-encoding"))
                implementation(npm("abort-controller"))

                implementation("io.ktor:ktor-client-js:1.3.0") //include http&websockets
                //todo: bugfix in ktor-client?
                implementation(npm("bufferutil")) //TODO: Uncomment this and stuff breaks. WHY?
                implementation(npm("utf-8-validate"))

                //ktor client js json
                implementation("io.ktor:ktor-client-json-js:1.3.0")
                implementation("io.ktor:ktor-client-serialization-js:1.3.0")

                //React, React DOM + Wrappers (chapter 3)
                implementation("org.jetbrains:kotlin-react:16.9.0-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains:kotlin-react-dom:16.9.0-pre.89-kotlin-1.3.60")
                implementation(npm("react", "16.12.0"))
                implementation(npm("react-dom", "16.12.0"))

                //Kotlin Styled (chapter 3)
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.90-kotlin-1.3.61")
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