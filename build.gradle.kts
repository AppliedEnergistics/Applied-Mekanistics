import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("dev.architectury.loom") version "0.10.0-SNAPSHOT"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    `maven-publish`
}

group = "me.ramidzkh"
version = "1.0.${System.getenv("BUILD_NUMBER") ?: "0-SNAPSHOT"}"

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
    forge("net.minecraftforge", "forge", "1.18.1-39.0.61")

    // We depend on many AE2 internals, such as using their basic cell drive, to straight out implementation details
    // (addressed by https://github.com/AppliedEnergistics/Applied-Energistics-2/pull/6041)
    // modCompileOnly("appeng", "appliedenergistics2", "10.0.1", classifier = "api")
    modImplementation("appeng", "appliedenergistics2", "10.0.1")

    modCompileOnly("mekanism", "Mekanism", "1.18.1-10.1.1.homebaked", classifier = "api")
    modRuntimeOnly("mekanism", "Mekanism", "1.18.1-10.1.1.homebaked", classifier = "all")

    modCompileOnly("mezz.jei", "jei-1.18.1", "9.2.3.82", classifier = "api")
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

////////////////
// CurseForge
System.getenv("CURSEFORGE")?.let {
    apply(plugin = "curseforge")

    curseforge {
        apiKey = it

        project(closureOf<CurseProject> {
            id = "574300"
            changelogType = "markdown"
            changelog = System.getenv("CHANGELOG")
            releaseType = if (System.getenv("CHANGELOG").contains("[beta]")) "beta" else "alpha"
            addGameVersion("1.18.1")
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
