package io.github.xffc.packetcatcher

import io.github.xffc.packetcatcher.config.ModConfig
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.world.InteractionResult
import java.time.format.DateTimeFormatter

object PacketCatcherMod : ClientModInitializer {
    private lateinit var config: ModConfig

    override fun onInitializeClient() {
        val holder = AutoConfig.register(ModConfig::class.java) { definition, configClass ->
            Toml4jConfigSerializer(definition, configClass)
        }
        
        holder.registerSaveListener { _, config ->
            PacketLogger.enabled = config.enabled
            PacketLogger.titleFormat = config.titleFormat
            PacketLogger.valueFormat = config.valueFormat
            PacketLogger.dateFormatter = DateTimeFormatter.ofPattern(config.timeFormat)
            PacketLogger.filters = mapOf(
                PacketFlow.CLIENTBOUND to (config.clientboundFilter.type to config.clientboundFilter.entries.split(";")),
                PacketFlow.SERVERBOUND to (config.serverboundFilter.type to config.serverboundFilter.entries.split(";"))
            )

            InteractionResult.SUCCESS
        }

        config = holder.config
    }
}