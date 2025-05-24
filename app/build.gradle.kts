import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec

plugins {
    java
    war
}

group = "org.example"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java.srcDirs(properties["src.dir"] as String)
        resources.srcDirs(properties["resources.dir"] as String)
    }
    test {
        java.srcDirs(properties["test.dir"] as String)
        resources.srcDirs(properties["resources.dir"] as String)
    }
}

dependencies {
    implementation("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.0")
    implementation("org.json:json:20231013")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("org.hibernate.orm:hibernate-core:6.6.1.Final")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")
    implementation("org.hibernate.validator:hibernate-validator:8.0.2.Final")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")
    compileOnly("jakarta.servlet.jsp:jakarta.servlet.jsp-api:4.0.0")
    compileOnly("jakarta.el:jakarta.el-api:6.0.0")
    compileOnly("jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0")
    compileOnly("org.glassfish:jakarta.faces:4.0.7")
    compileOnly("jakarta.platform:jakarta.jakartaee-web-api:10.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testImplementation("org.junit.platform:junit-platform-launcher:1.11.3")
    testImplementation("org.junit.platform:junit-platform-commons:1.11.3")
    testImplementation("org.opentest4j:opentest4j:1.3.0")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
}

tasks.jar {
    archiveBaseName.set("web-lab3")
    manifest {
        attributes(
            "Created-By" to "Kanye West",
            "Manifest-Version" to "1.0",
            "Main-Class" to "NoClass"
        )
    }
}

tasks.war {
    archiveBaseName.set("web-lab3")
    webXml = file("${properties["webapp.dir"]}/WEB-INF/web.xml")
    from(properties["webapp.dir"] as String)
    filesMatching("**/*") {
        into("")
    }
}

tasks.register<Copy>("copyResources") {
    from(properties["resources.dir"] as String)
    into(properties["build.main.classes.dir"] as String)
}

tasks.register<Copy>("copyWebapp") {
    from(properties["webapp.dir"] as String)
    into(properties["build.dir"] as String)
}

tasks.register<Copy>("copyLibs") {
    from(properties["lib.dir"] as String)
    into(properties["build.dir.lib"] as String)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<War> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.clean {
    delete(properties["build.dir"], properties["lib.dir"], properties["test.reports.dir"])
}

tasks.register<Delete>("clean_all") {
    dependsOn(tasks.clean)
    delete("${properties["test.reports.dir"]}/html", properties["message.locale"])
}

tasks.test {
    useJUnitPlatform()
    reports {
        html.required.set(true)
        junitXml.required.set(true)
        junitXml.outputLocation.set(file(properties["test.reports.dir"] as String))
    }
}

tasks.register<Exec>("music") {
    val currentTime = System.currentTimeMillis() % 1000
    val musicFile = if (currentTime in 0..499) "surprise1.mp3" else "surprise2.mp3"
    println("Selected music file: $musicFile")
    commandLine("mpg123", "${properties["music.dir"]}/$musicFile")
    onlyIf { System.getProperty("os.name").lowercase().contains("linux") }
}

tasks.register("music_build") {
    dependsOn(tasks.build, "music")
}


tasks.register("lang") {
    delete(properties["message.locale"].toString())
    doLast {
        val supportedLangs = listOf("en", "ru")
        val selectedLang: String =when {
            project.hasProperty("lang") && project.property("lang") !is Task -> {
                val inputLang = project.property("lang").toString()
                inputLang
            }
            else -> "ru"
        }

        if (selectedLang !in supportedLangs) {
            throw GradleException("Ошибка: локализация '$selectedLang' не поддерживается. Доступные: $supportedLangs")
        }

        val targetDir = properties["message.locale"].toString()

        val sourceDir = properties["message.resources"].toString()
        val sourceFile = file("$sourceDir/messages_$selectedLang.properties")

        file(targetDir).mkdirs()

        project.copy {
            from(sourceFile)
            into(targetDir)
        }

        println("Выбрана локализация: $selectedLang")
    }
}

tasks.register<Exec>("start_wf") {
    commandLine("sh", "${properties["wf.start.script"]}")
}

tasks.register<Exec>("stop_wf") {
    commandLine("sh", "${properties["wf.stop.script"]}", "--connect", "command=:shutdown")
}

val checkAndStartWf = tasks.register("check_and_start_wf") {
    group = "wildfly"
    description = "Checks if WildFly is running, and starts it if not"

    doLast {
        val isRunning = try {
            val proc = ProcessBuilder("pgrep", "-f", "wildfly.*standalone")
                .redirectErrorStream(true)
                .start()
            proc.inputStream.bufferedReader().readText().isNotBlank()
        } catch (e: Exception) {
            false
        }

        if (isRunning) {
            println("WildFly is already running.")
        } else {
            println("WildFly is not running. Starting...")
            exec {
                commandLine("sh", "${properties["wf.start.script"]}")
            }
            println("Waiting for WildFly to start...")
        }
    }
}

tasks.register("env") {
    group = "deployment"
    description = "Builds, prepares WAR, ensures WildFly is up, and deploys"

    dependsOn(tasks["lang"], tasks.war,  checkAndStartWf)

    doLast {
        println("Copying WAR...")
        project.copy {
            from(properties["war.file"] as String)
            into(properties["scp.dir"] as String)
        }
        println("WAR copied successfully.")
    }
}
