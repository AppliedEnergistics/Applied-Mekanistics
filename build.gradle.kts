import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("dev.architectury.loom") version "0.10.0-SNAPSHOT"
    `maven-publish`
}

group = "me.ramidzkh"
version = "1.0.0-SNAPSHOT"

repositories {
    maven {
        name = "Modmaven"
        url = uri("https://modmaven.dev/")

        content {
            includeGroup("appeng")
        }
    }

    maven {
        name = "Progwml6 maven"
        url = uri("https://dvs1.progwml6.com/files/maven/")

        content {
            includeGroup("mezz.jei")
        }
    }

    maven {
        name = "Local"
        url = file("libs").toURI()
    }
}

dependencies {
    minecraft("net.minecraft", "minecraft", "1.18.1")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge", "forge", "1.18.1-39.0.59")

    // modCompileOnly("appeng", "appliedenergistics2", "10.0.1", classifier = "api")
    modImplementation("appeng", "appliedenergistics2", "10.0.1")

    modCompileOnly("mekanism", "Mekanism", "1.18.1-10.1.1.homebaked", classifier = "api")
    modRuntimeOnly("mekanism", "Mekanism", "1.18.1-10.1.1.homebaked", classifier = "all")

    // modCompileOnly("mezz.jei", "jei-1.18.1", "9.2.3.82", classifier = "api")
    modRuntimeOnly("mezz.jei", "jei-1.18.1", "9.2.3.82")
}

sourceSets {
    main {
        resources {
            srcDir("src/main/generated")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

loom {
    silentMojangMappingsLicense()

    forge {
        mixinConfigs("ae2-mekanism-addons.mixins.json")

        dataGen {
            mod("ae2_mekanism_addons")
        }
    }

    launches {
        named("data") {
            arg("--existing", file("src/main/resources").absolutePath)
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    processResources {
        inputs.property("version", project.version)

        exclude(".cache")

        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }

    jar {
        from(project.file("LICENSE"))

        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to project.name,
                    "Specification-Vendor" to "ramidzkh",
                    "Specification-Version" to "1",
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to "ramidzkh",
                    "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
                )
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mod") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = uri("${buildDir}/repos/releases")
            val snapshotsRepoUrl = uri("${buildDir}/repos/snapshots")
            name = "Project"
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}
