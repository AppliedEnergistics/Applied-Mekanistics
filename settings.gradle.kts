rootProject.name = "Applied-Mekanistics"

pluginManagement {
    repositories {
        maven {
            name = "FabricMC"
            url = uri("https://maven.fabricmc.net/")
        }

        maven {
            name = "architectury"
            url = uri("https://maven.architectury.dev/")
        }

        maven {
            name = "MinecraftForge"
            url = uri("https://files.minecraftforge.net/maven/")
        }

        gradlePluginPortal()
    }
}
