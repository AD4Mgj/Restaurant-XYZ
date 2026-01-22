plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "kul.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.openjfx:javafx-controls:17.0.6")
    implementation("org.openjfx:javafx-fxml:17.0.6")
    implementation("org.openjfx:javafx-web:17.0.6")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("kul.example.HelloApplication")
}