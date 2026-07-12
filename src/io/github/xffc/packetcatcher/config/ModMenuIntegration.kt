package io.github.xffc.packetcatcher.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.autoconfig.AutoConfigClient
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ModMenuIntegration: ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = { parent ->
        AutoConfigClient.getConfigScreen(ModConfig::class.java, parent).get()
    }
}