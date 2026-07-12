import xyz.jpenilla.resourcefactory.fabric.Environment

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.loom)
    alias(libs.plugins.resourcefactory)
}

group = "io.github.xffc"
version = "1.0"

repositories {
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)
    implementation(libs.fabric.kotlin)
    implementation(libs.clothconfig)
    implementation(libs.modmenu)
}

sourceSets.main {
    kotlin.srcDir("src")
    java.srcDir("src")
    resources.srcDir("resources")
}

fabricModJson {
    name = "Packet catcher"
    description = "Mod for catching packets"
    contact.sources = "https://github.com/xffc/packetcatcher"
    author("xffc") { contact.homepage = "https://github.com/xffc" }
    mitLicense()

    environment = Environment.CLIENT
    clientEntrypoint("$group.${project.name}.PacketCatcherMod") { adapter = "kotlin" }
    entrypoint("modmenu", "$group.${project.name}.config.ModMenuIntegration")

    mixin("${project.name}.mixins.json") { environment = Environment.CLIENT }

    depends("fabricloader", ">=${libs.versions.fabric.loader.get()}")
    depends("minecraft", "~${libs.versions.minecraft.get()}")
    depends("java", ">=25")
    depends("fabric-api", "*")
}