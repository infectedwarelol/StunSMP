plugins {
    java
}

group = property("group") as String
version = property("version") as String

val paperApiVersion = project.property("paperApiVersion") as String

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperApiVersion")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

val generateTemplates = tasks.register<Copy>("generateTemplates") {
    group = "build"
    val properties = mapOf("version" to project.version)
    inputs.properties(properties)
    filteringCharset = "UTF-8"
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/templates"))
    expand(properties)
}

sourceSets.main {
    resources.srcDir(generateTemplates)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
    options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
}
