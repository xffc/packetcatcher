package io.github.xffc.packetcatcher

import io.github.xffc.packetcatcher.config.ModConfig
import io.github.xffc.packetcatcher.config.ModConfig.Companion.configHandler
import net.fabricmc.api.ClientModInitializer

object PacketCatcherMod : ClientModInitializer {
    lateinit var configInstance: ModConfig

    override fun onInitializeClient() {
        configHandler.load()
        configHandler.save()
        configInstance = configHandler.instance()
    }
}