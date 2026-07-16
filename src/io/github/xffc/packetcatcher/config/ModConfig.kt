package io.github.xffc.packetcatcher.config

import com.google.gson.GsonBuilder
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.ConfigField
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen
import dev.isxander.yacl3.config.v2.api.autogen.EnumCycler
import dev.isxander.yacl3.config.v2.api.autogen.ListGroup
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess
import dev.isxander.yacl3.config.v2.api.autogen.StringField
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import io.github.xffc.packetcatcher.PacketLogger
import io.github.xffc.packetcatcher.translatable
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import java.time.format.DateTimeFormatter
import dev.isxander.yacl3.config.v2.api.autogen.Boolean as Bool

class ModConfig {
    companion object {
        val configHandler: ConfigClassHandler<ModConfig> = ConfigClassHandler.createBuilder(ModConfig::class.java)
            .id(Identifier.fromNamespaceAndPath("packetcatcher", "config"))
            .serializer { config ->
                GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().configDir.resolve("packetcatcher.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build()
            }
            .build()
    }

    @AutoGen(category = "main")
    @Bool(formatter = Bool.Formatter.ON_OFF, colored = true)
    @SerialEntry
    var enabled: Boolean = false

    @AutoGen(category = "main")
    @StringField
    @SerialEntry
    var titleFormat: String = "[%time%] %type% %id%%nl%%values%"

    @AutoGen(category = "main")
    @StringField
    @SerialEntry
    var valueFormat: String = "%name% : %value%"

    @AutoGen(category = "main")
    @StringField
    @SerialEntry
    var timeFormat: String = "dd.MM.yyyy HH:mm:ss:SSS"

    @AutoGen(category = "serverbound")
    @ListGroup(valueFactory = EntriesListFactory::class, controllerFactory = EntriesListFactory::class)
    @SerialEntry
    var serverboundEntries: MutableList<String> = mutableListOf()

    @AutoGen(category = "serverbound")
    @EnumCycler
    @SerialEntry
    var serverboundFilter: FilterType = FilterType.BLACKLIST

    @AutoGen(category = "clientbound")
    @ListGroup(valueFactory = EntriesListFactory::class, controllerFactory = EntriesListFactory::class)
    @SerialEntry
    var clientboundEntries: MutableList<String> = mutableListOf()

    @AutoGen(category = "clientbound")
    @EnumCycler
    @SerialEntry
    var clientboundFilter: FilterType = FilterType.BLACKLIST

    enum class FilterType: NameableEnum {
        WHITELIST,
        BLACKLIST;

        override fun getDisplayName(): Component =
            "yacl3.config.packetcatcher:config.filterType.${this.name.lowercase()}".translatable
    }

    class EntriesListFactory: ListGroup.ValueFactory<String>, ListGroup.ControllerFactory<String> {
        override fun createController(
            annotation: ListGroup,
            field: ConfigField<List<String>>,
            storage: OptionAccess,
            option: Option<String>
        ): ControllerBuilder<String> = StringControllerBuilder.create(option)

        override fun provideNewValue(): String = ""
    }
}