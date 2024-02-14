plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit test framework.
    testImplementation(libs.junit)

    // This dependency is used by the application.
    implementation(libs.guava)

    // https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    implementation("com.sparkjava:spark-core:2.9.3")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    // Define the main class for the application.
    mainClass = "Main"
}

// Define a task to copy the React build files into the resources directory
tasks.register<Copy>("copyReactBuild") {
    from("frontend/build") // Adjust this path based on the location of your React build
    into("src/main/resources/static") // This is the directory where Gradle serves static files
}

// Ensure that the task is executed before running the application
tasks.named("run") {
    dependsOn("copyReactBuild")
}
