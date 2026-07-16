package io.github.xffc.packetcatcher

import io.github.xffc.packetcatcher.config.ModConfig
import io.netty.channel.Channel
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import java.io.File
import java.lang.reflect.Modifier
import java.net.InetSocketAddress
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PacketLogger {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private var files: MutableMap<Channel, File> = mutableMapOf()

    @JvmStatic
    fun write(ctx: Channel, packet: Packet<*>) = scope.launch {
        writeAsync(ctx, packet)
    }

    @JvmStatic
    suspend fun writeAsync(ctx: Channel, packet: Packet<*>) {
        if (!PacketCatcherMod.configInstance.enabled) return

        val packetId = packet.type().id.toString()

        val filter = when (packet.type().flow) {
            PacketFlow.CLIENTBOUND -> PacketCatcherMod.configInstance.clientboundEntries to PacketCatcherMod.configInstance.clientboundFilter
            PacketFlow.SERVERBOUND -> PacketCatcherMod.configInstance.serverboundEntries to PacketCatcherMod.configInstance.serverboundFilter
        }

        val passed = when (filter.second) {
            ModConfig.FilterType.WHITELIST -> packetId in filter.first
            ModConfig.FilterType.BLACKLIST -> packetId !in filter.first
        }

        if (!passed) return

        mutex.withLock {
            withContext(Dispatchers.IO) {
                val file = files[ctx] ?: return@withContext null

                val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern(PacketCatcherMod.configInstance.timeFormat))

                val values = packet::class.java.declaredFields.mapNotNull {
                    if (!it.trySetAccessible() || Modifier.isStatic(it.modifiers)) return@mapNotNull null
                    val value = it.get(packet) ?: return@mapNotNull null
                    PacketCatcherMod.configInstance.valueFormat
                        .replace("%name%", it.name)
                        .replace("%value%", value.toString())
                }.joinToString(System.lineSeparator())

                val text = PacketCatcherMod.configInstance.titleFormat
                    .replace("%type%", packet.type().flow.id())
                    .replace("%time%", time)
                    .replace("%id%", packetId)
                    .replace("%nl%", System.lineSeparator())
                    .replace("%values%", values)

                file.appendText(text + System.lineSeparator())
            }
        }
    }

    @JvmStatic
    fun start(ctx: Channel) {
        if (!PacketCatcherMod.configInstance.enabled) return
        val host = ctx.remoteAddress() as InetSocketAddress
        files[ctx] = File("PD-${host.hostString}-${System.currentTimeMillis() / 1000}.txt")
    }

    @JvmStatic
    fun stop(ctx: Channel) {
        scope.launch {
            mutex.withLock {
                files.remove(ctx)
            }
        }
    }
}