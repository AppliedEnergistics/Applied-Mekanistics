import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("dev.architectury.loom") version "0.11.0-SNAPSHOT"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("com.diffplug.spotless") version "6.4.1"
    `maven-publish`
}

group = "me.ramidzkh"
version = System.getenv("APPMEK_VERSION") ?: "0.0.0"

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
    minecraft("net.minecraft", "minecraft", "1.18.2")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge", "forge", "1.18.2-40.0.32")

    // We depend on many AE2 internals, such as using their basic cell drive, thus not using classifier = "api"
    modImplementation("appeng", "appliedenergistics2", "11.0.0-alpha.3")

    // We depend on mekanism internals, thus not using classifier = "api"
    modCompileOnly("mekanism", "Mekanism", "1.18.2-10.1.2.homebaked", classifier = "all")
    modRuntimeOnly("mekanism", "Mekanism", "1.18.2-10.1.2.homebaked", classifier = "all")

    modCompileOnly("mezz.jei", "jei-1.18.2", "9.5.5.174", classifier = "api")
    modRuntimeOnly("mezz.jei", "jei-1.18.2", "9.5.5.174")
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
        mixinConfigs("appmek.mixins.json")

        dataGen {
            mod("appmek")
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

/////////////
// Spotless
spotless {
    java {
        target("src/main/java/**/*.java")

        endWithNewline()
        indentWithSpaces()
        removeUnusedImports()
        toggleOffOn()
        eclipse().configFile("codeformat/codeformat.xml")
        importOrderFile("codeformat/ae2.importorder")
    }

    format("json") {
        target("src/*/resources/**/*.json")
        targetExclude("src/generated/resources/**")
        prettier().config(mapOf("parser" to "json"))
    }
}

////////////////
// CurseForge
System.getenv("CURSEFORGE")?.let {
    apply(plugin = "curseforge")

    curseforge {
        apiKey = it

        project(closureOf<CurseProject> {
            val version = version.toString()

            id = "574300"
            changelogType = "markdown"
            changelog = "View changelog at [the release page](https://github.com/AppliedEnergistics/Applied-Mekanistics/releases/tag/${version})"
            releaseType = when {
                version.contains("alpha") -> "alpha"
                version.contains("beta") -> "beta"
                else -> "release"
            }

            addGameVersion("1.18.2")
            addGameVersion("Forge")

            mainArtifact(tasks.remapJar.flatMap { it.archiveFile }, closureOf<CurseArtifact> {
                displayName = "${project.version}"

                relations(closureOf<CurseRelation> {
                    requiredDependency("applied-energistics-2")
                    requiredDependency("mekanism")
                })
            })
        })
    }
}
