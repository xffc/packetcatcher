package io.github.xffc.packetcatcher.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Config(name = "packetcatcher")
class ModConfig: ConfigData {
    val enabled: Boolean = false

    val titleFormat = "[%time%] %type% %id%%nl%%values%"
    val valueFormat: String = "%name% : %value%"
    val timeFormat: String = "dd.MM.yyyy HH:mm:ss:SSS"

    @ConfigEntry.Gui.CollapsibleObject
    val serverboundFilter: Filter = Filter()

    @ConfigEntry.Gui.CollapsibleObject
    val clientboundFilter: Filter = Filter()

    class Filter {
        val entries: String = ""

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        val type: Type = Type.BLACKLIST

        enum class Type {
            WHITELIST, BLACKLIST
        }
    }
}