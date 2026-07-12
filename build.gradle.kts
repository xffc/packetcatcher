import xyz.jpenilla.resourcefactory.fabric.Environment
import xyz.jpenilla.resourcefactory.fabric.fabricModJson

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.loom)
    alias(libs.plugins.resources)
}

group = "io.github.xffc"
version = "1.0"

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)
}

sourceSets.main {
    kotlin.srcDir("src")
    resources.srcDir("resources")
}

fabricModJson {
    name = "Packet Catcher"
    description = "Mod for catching packets"
    author("xffc")
    contact.sources = "https://github.com/xffc/packetcatcher"
    mitLicense()

    environment = Environment.ANY
    mainEntrypoint("")
}